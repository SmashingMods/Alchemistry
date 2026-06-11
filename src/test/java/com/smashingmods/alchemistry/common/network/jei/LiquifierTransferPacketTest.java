package com.smashingmods.alchemistry.common.network.jei;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipeSerializer;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins {@link LiquifierTransferPacket}'s server-side composition piece by piece, exactly as
 * {@code handle()} runs it, against a recipe decoded from the shipped datapack JSON shape (an
 * {@code input} of {@code {count: 8, ingredient: {item: ...}}} and a fluid {@code result}, the
 * shape of every {@code data/alchemistry/recipe/liquifier/*.json}). The liquifier's JEI "+" went
 * completely dead in the field -- survival and creative, while manual insertion worked -- and the
 * hand-test investigation could not localize it, so each hop the click crosses is pinned here:
 * the recipe's serializer stream round-trip (the client recipe sync), the packet's own stream
 * round-trip, the registry-lookup predicate run on the twice-round-tripped input, the joint match
 * over the player inventory, and the {@code canTransfer} tank gate.
 *
 * <p>The actual break was the tank gate: {@code canTransfer} required the output tank to be EMPTY
 * outright, and unlike the item machines -- whose output slots the handler first empties into the
 * player's inventory -- a fluid tank cannot be emptied that way, so the fluid produced by the
 * machine's very first operation permanently vetoed every later transfer in both game modes.
 * {@link #tankGate_fluidProducedByTheMachineMustNotVetoTheTransfer} reproduces that failure (it
 * fails against the old empty-only gate) and pins the fix: the gate now mirrors
 * {@code LiquifierBlockEntity#canProcessRecipe}'s fluid acceptance, refusing only a tank occupied
 * by a DIFFERENT fluid. The in-world half -- a liquifier that has already produced fluid still
 * accepting the transfer through the real {@code handle()} -- is covered by the
 * {@code liquifierTransferWithProducedFluidStillTransfers} gametest.</p>
 */
class LiquifierTransferPacketTest extends BootstrappedTest {

    /**
     * The shipped liquifier recipe shape (e.g. {@code recipe/liquifier/hydrogen.json}) with the
     * chemlib element swapped for a vanilla item -- mod items would need an unfrozen registry (see
     * {@link BootstrappedTest}) -- and the modded fluid for water. Count 8 and amount 500 are the
     * shipped values.
     */
    private static final String RECIPE_JSON = """
            {
              "type": "alchemistry:liquifier",
              "group": "alchemistry:liquifier",
              "input": {
                "count": 8,
                "ingredient": "minecraft:iron_ingot"
              },
              "result": {
                "amount": 500,
                "fluid": "minecraft:water"
              }
            }""";

    @Test
    void lookupPredicate_findsTheRecipeAcrossTheFullNetworkRoundTrip() {
        // The server recipe, decoded from the datapack JSON the way RecipeManager does.
        LiquifierRecipe serverRecipe = decodeDatapackRecipe();

        // The client's copy of the recipe arrives through the serializer's stream codec (the
        // recipe sync packet); JEI hands transferRecipe that copy, whose getInput() the packet
        // carries back to the server through its own stream codec. Round-trip both hops.
        LiquifierRecipe clientRecipe = roundTripRecipe(serverRecipe);
        LiquifierTransferPacket packet = new LiquifierTransferPacket(new BlockPos(1, 2, 3), clientRecipe.getInput(), false);

        RegistryFriendlyByteBuf buffer = registryBuffer();
        LiquifierTransferPacket.STREAM_CODEC.encode(buffer, packet);
        IngredientStack received = readPacketInput(buffer);

        // The twice-round-tripped input must resolve the recipe it came from, and must not match
        // a recipe for a different element -- the registry lookup takes the first predicate hit.
        assertTrue(LiquifierTransferPacket.matchesTransferredInput(serverRecipe, received),
                "the round-tripped input no longer matches its own recipe");
        LiquifierRecipe otherRecipe = new LiquifierRecipe(serverRecipe.getId(), serverRecipe.getGroup(),
                new IngredientStack(Items.GOLD_INGOT, 8), new FluidStack(Fluids.WATER, 500));
        assertFalse(LiquifierTransferPacket.matchesTransferredInput(otherRecipe, received),
                "the round-tripped input must not match a different element's recipe");
    }

    @Test
    void tankGate_fluidProducedByTheMachineMustNotVetoTheTransfer() {
        LiquifierRecipe recipe = decodeDatapackRecipe();

        // The field failure: after one manual operation the tank holds the recipe's own output
        // (500 mB here), and the old empty-only gate refused every transfer from then on.
        FluidStack producedByThisRecipe = recipe.getOutput().copy();
        assertTrue(LiquifierTransferPacket.tankAccepts(producedByThisRecipe, recipe.getOutput()),
                "a tank holding the recipe's own output fluid must not block the transfer");

        // An empty tank keeps accepting, and a tank occupied by a different fluid still refuses --
        // the machine could not process the recipe until that fluid is drained, exactly the
        // condition canProcessRecipe enforces.
        assertTrue(LiquifierTransferPacket.tankAccepts(FluidStack.EMPTY, recipe.getOutput()));
        assertFalse(LiquifierTransferPacket.tankAccepts(new FluidStack(Fluids.LAVA, 100), recipe.getOutput()),
                "a tank occupied by a different fluid must keep refusing the transfer");
    }

    @Test
    void survivalMatch_count8Input_claimsAStackAndBoundsOperations() {
        LiquifierRecipe recipe = decodeDatapackRecipe();
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.IRON_INGOT, 64));

        List<IngredientStack> ingredients = LiquifierTransferPacket.buildRecipeIngredients(recipe);
        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // The count-8 input claims the 64-stack; a single transfer moves one operation's 8 and a
        // max transfer the full 64 / 8 = 8 operations.
        assertTrue(TransferUtils.isFullMatch(matched));
        assertEquals(2, matched.get(0).slot());
        assertEquals(1, TransferUtils.getMaxOperations(matched, ingredients, false));
        assertEquals(8, TransferUtils.getMaxOperations(matched, ingredients, true));
    }

    /**
     * Decodes {@link #RECIPE_JSON} through the serializer's disk codec, exactly as RecipeManager
     * decodes the datapack file -- including the {@link RegistryOps} wrap, which this version's
     * holder-backed {@code Ingredient.CODEC} needs to resolve the item id. The serializer is
     * constructed the way the registry binds it; the codec ignores the dispatcher's {@code type}
     * key, which RecipeManager strips before decoding.
     */
    private static LiquifierRecipe decodeDatapackRecipe() {
        JsonObject json = JsonParser.parseString(RECIPE_JSON).getAsJsonObject();
        return new LiquifierRecipeSerializer<>(LiquifierRecipe::new).codec().codec()
                .parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess()), json)
                .getOrThrow(message -> new AssertionError("datapack-shaped recipe failed to decode: " + message));
    }

    /**
     * One pass through the serializer's stream codec -- the wire trip a recipe takes to the client.
     */
    private static LiquifierRecipe roundTripRecipe(LiquifierRecipe pRecipe) {
        RegistryFriendlyByteBuf buffer = registryBuffer();
        LiquifierRecipeSerializer<LiquifierRecipe> serializer = new LiquifierRecipeSerializer<>(LiquifierRecipe::new);
        serializer.streamCodec().encode(buffer, pRecipe);
        return serializer.streamCodec().decode(buffer);
    }

    /**
     * Re-reads an encoded {@link LiquifierTransferPacket} field by field (BlockPos, input,
     * maxTransfer -- the codec's composite order) and returns the input as the server receives it.
     * The packet's fields are private with no getters, so the buffer is the observable surface.
     */
    private static IngredientStack readPacketInput(RegistryFriendlyByteBuf pBuffer) {
        BlockPos.STREAM_CODEC.decode(pBuffer);
        return AlchemistryRecipeCodecs.INGREDIENT_STACK_STREAM_CODEC.decode(pBuffer);
    }
}
