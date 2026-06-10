package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipeSerializer;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Round-trip contract for the wire path {@link SyncRecipesPacket} actually rides: a {@code List<RecipeHolder<?>>}
 * through {@link SyncRecipesPacket#STREAM_CODEC}, which composes {@link RecipeHolder#STREAM_CODEC} over
 * {@link net.minecraft.network.codec.ByteBufCodecs#list()}. That holder codec is the one every client join runs --
 * it writes each recipe's serializer <em>registry id</em> (via {@code Recipe.STREAM_CODEC}'s dispatch over
 * {@link Registries#RECIPE_SERIALIZER}) and re-looks it up on decode -- so a serializer the registry can't resolve,
 * or an id/key that doesn't survive, is the "crash on join" failure mode. The per-serializer
 * {@code RecipeSerializerStreamCodecRoundTripTest} drives each {@code streamCodec()} directly and never exercises
 * that dispatch; {@code PacketStreamCodecRoundTripTest} explicitly excludes this packet. This test is the only one
 * that puts a holder list through the registry-backed dispatch end to end.
 *
 * <p>{@link RecipeHolder#STREAM_CODEC} dispatches on {@code recipe.getSerializer()}, which on every Alchemistry
 * recipe resolves through {@code RecipeRegistry}'s {@code DeferredHolder}s against {@link BuiltInRegistries#RECIPE_SERIALIZER}.
 * {@code Bootstrap.bootStrap()} (run by {@link BootstrappedTest}) does not register the mod's serializers, so this
 * class registers the seven into that registry once before its tests -- unfreezing it, registering, and refreezing,
 * the freeze/unfreeze cycle NeoForge itself uses on vanilla registries -- so the dispatch can both encode an id and
 * decode it back. Item/fluid content encodes by registry id too, hence {@link BootstrappedTest} for the
 * registry-backed buffer and vanilla {@code Items.*}/{@code Fluids.*} content.</p>
 */
class SyncRecipesPacketWireRoundTripTest extends BootstrappedTest {

    private static final String GROUP = "test";

    /**
     * Registers Alchemistry's seven recipe serializers into {@link BuiltInRegistries#RECIPE_SERIALIZER} so the
     * recipes' {@code getSerializer()} DeferredHolders bind to them and {@link Recipe#STREAM_CODEC}'s id dispatch
     * resolves. Each serializer is constructed exactly as the registry binds it ({@code new XSerializer<>(XRecipe::new)},
     * as {@code RecipeRegistry} does and {@code RecipeSerializerStreamCodecRoundTripTest} mirrors); the binding is by
     * registry key, so the registered instance is the one {@code RecipeRegistry.X_SERIALIZER.get()} hands back.
     * Runs after {@code BootstrappedTest.bootstrap()} (a superclass {@code @BeforeAll} runs first), which froze the
     * registry; the registry is concretely a {@code MappedRegistry}, whose {@code unfreeze}/{@code freeze} NeoForge
     * cycles on vanilla registries to splice in modded entries. Both are reached reflectively to keep the test off
     * platform-internal registry types, matching {@link BootstrappedTest}'s reflective registry setup.
     */
    @BeforeAll
    static void registerSerializers() {
        Registry<RecipeSerializer<?>> registry = BuiltInRegistries.RECIPE_SERIALIZER;
        setFrozen(registry, false);
        registerIfAbsent(registry, "atomizer", new AtomizerRecipeSerializer<>(AtomizerRecipe::new));
        registerIfAbsent(registry, "compactor", new CompactorRecipeSerializer<>(CompactorRecipe::new));
        registerIfAbsent(registry, "combiner", new CombinerRecipeSerializer<>(CombinerRecipe::new));
        registerIfAbsent(registry, "dissolver", new DissolverRecipeSerializer<>(DissolverRecipe::new));
        registerIfAbsent(registry, "fission", new FissionRecipeSerializer<>(FissionRecipe::new));
        registerIfAbsent(registry, "fusion", new FusionRecipeSerializer<>(FusionRecipe::new));
        registerIfAbsent(registry, "liquifier", new LiquifierRecipeSerializer<>(LiquifierRecipe::new));
        setFrozen(registry, true);
    }

    @Test
    void syncRecipesPacket_roundTripsAHolderPerMachineTypePreservingIdsAndKeyFields() {
        List<RecipeHolder<?>> holders = List.of(
                holder("atomizer/snowball",
                        new AtomizerRecipe(unkeyed(), GROUP, new FluidStack(Fluids.WATER, 500), new ItemStack(Items.SNOWBALL))),
                holder("compactor/iron_block",
                        new CompactorRecipe(unkeyed(), GROUP, new IngredientStack(Items.IRON_INGOT), new ItemStack(Items.IRON_BLOCK))),
                holder("combiner/netherite",
                        new CombinerRecipe(unkeyed(), GROUP, inputSet(Items.IRON_INGOT, Items.GOLD_INGOT), new ItemStack(Items.NETHERITE_INGOT))),
                holder("dissolver/iron_ingot",
                        new DissolverRecipe(unkeyed(), GROUP, new IngredientStack(Items.IRON_INGOT), weightedSetWithNothing())),
                holder("fission/iron",
                        new FissionRecipe(unkeyed(), GROUP, new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.COPPER_INGOT))),
                holder("fusion/netherite",
                        new FusionRecipe(unkeyed(), GROUP, new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.NETHERITE_INGOT))),
                holder("liquifier/water",
                        new LiquifierRecipe(unkeyed(), GROUP, new IngredientStack(Items.ICE), new FluidStack(Fluids.WATER, 500)))
        );

        // Drive the actual packet codec: encode the holder list, then decode it from a registry-backed buffer.
        RegistryFriendlyByteBuf buffer = registryBuffer();
        SyncRecipesPacket.STREAM_CODEC.encode(buffer, new SyncRecipesPacket(holders));
        List<RecipeHolder<?>> decoded = decodeRecipes(buffer);

        assertEquals(holders.size(), decoded.size(), "every holder must survive the round trip");
        for (int i = 0; i < holders.size(); i++) {
            RecipeHolder<?> original = holders.get(i);
            RecipeHolder<?> roundTripped = decoded.get(i);
            // The holder key (a ResourceKey<Recipe<?>>) is the recipe identity; it must come back byte-for-byte.
            // RecipeHolder.STREAM_CODEC dispatches the value over the serializer registry id, so the decoded recipe
            // resolving to the same serializer instance is the proof that write-id/re-look-up survived the wire.
            assertEquals(original.id(), roundTripped.id());
            assertEquals(original.value().getClass(), roundTripped.value().getClass());
            assertEquals(original.value().getSerializer(), roundTripped.value().getSerializer());
        }

        // Spot-check the key fields per type so the round trip is about content, not just cardinality.
        AtomizerRecipe atomizer = recipeAt(decoded, 0, AtomizerRecipe.class);
        assertEquals(Fluids.WATER, atomizer.getInput().getFluid());
        assertEquals(Items.SNOWBALL, atomizer.getOutput().getItem());

        CompactorRecipe compactor = recipeAt(decoded, 1, CompactorRecipe.class);
        assertEquals(Items.IRON_BLOCK, compactor.getOutput().getItem());

        CombinerRecipe combiner = recipeAt(decoded, 2, CombinerRecipe.class);
        assertEquals(2, combiner.getInput().size());
        assertEquals(Items.NETHERITE_INGOT, combiner.getOutput().getItem());

        FissionRecipe fission = recipeAt(decoded, 4, FissionRecipe.class);
        assertEquals(Items.IRON_INGOT, fission.getInput().getItem());
        assertEquals(Items.GOLD_INGOT, fission.getOutput1().getItem());
        assertEquals(Items.COPPER_INGOT, fission.getOutput2().getItem());

        FusionRecipe fusion = recipeAt(decoded, 5, FusionRecipe.class);
        assertEquals(Items.IRON_INGOT, fusion.getInput1().getItem());
        assertEquals(Items.GOLD_INGOT, fusion.getInput2().getItem());
        assertEquals(Items.NETHERITE_INGOT, fusion.getOutput().getItem());

        LiquifierRecipe liquifier = recipeAt(decoded, 6, LiquifierRecipe.class);
        assertEquals(Fluids.WATER, liquifier.getOutput().getFluid());

        // The dissolver output is a weighted ProbabilitySet whose "nothing" group is minecraft:air; the group
        // list, its weighted flag and the air group's probability all have to come back off the wire.
        DissolverRecipe dissolver = recipeAt(decoded, 3, DissolverRecipe.class);
        ProbabilitySet set = dissolver.getOutput();
        assertEquals(true, set.isWeighted());
        assertEquals(2, set.getProbabilityGroups().size());
        ProbabilityGroup nothing = set.getProbabilityGroups().get(1);
        assertEquals(Items.AIR, nothing.getOutput().get(0).getItem());
        assertEquals(25.0, nothing.getProbability());
    }

    private static ProbabilitySet weightedSetWithNothing() {
        // A weighted set with an explicit minecraft:air "nothing" roll alongside a real result.
        return ProbabilitySet.Builder.createSet()
                .weighted()
                .addGroup(75.0, new ItemStack(Items.IRON_NUGGET))
                .addGroup(25.0, new ItemStack(Items.AIR))
                .build();
    }

    private static <T extends AbstractProcessingRecipe> T recipeAt(List<RecipeHolder<?>> holders, int index, Class<T> type) {
        return assertInstanceOf(type, holders.get(index).value());
    }

    private static RecipeHolder<?> holder(String path, AbstractProcessingRecipe recipe) {
        return new RecipeHolder<>(key(path), recipe);
    }

    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath("alchemistry", path));
    }

    private static ResourceLocation unkeyed() {
        // The wire payload carries no recipe id (identity lives on the RecipeHolder), so the recipe value is
        // built with the placeholder id a freshly decoded recipe holds; only the holder key crosses the wire.
        return AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID;
    }

    private static Set<IngredientStack> inputSet(net.minecraft.world.item.Item... items) {
        Set<IngredientStack> inputs = new LinkedHashSet<>();
        for (net.minecraft.world.item.Item item : items) {
            inputs.add(new IngredientStack(item));
        }
        return inputs;
    }

    @SuppressWarnings("unchecked")
    private static List<RecipeHolder<?>> decodeRecipes(RegistryFriendlyByteBuf buffer) {
        // SyncRecipesPacket's only field is the holder list, decoded straight off the packet codec.
        SyncRecipesPacket packet = SyncRecipesPacket.STREAM_CODEC.decode(buffer);
        try {
            java.lang.reflect.Field field = SyncRecipesPacket.class.getDeclaredField("recipes");
            field.setAccessible(true);
            return (List<RecipeHolder<?>>) field.get(packet);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to read decoded recipes off SyncRecipesPacket", exception);
        }
    }

    private static void registerIfAbsent(Registry<RecipeSerializer<?>> registry, String path, RecipeSerializer<?> serializer) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("alchemistry", path);
        if (!registry.containsKey(id)) {
            Registry.register(registry, id, serializer);
        }
    }

    private static void setFrozen(Registry<RecipeSerializer<?>> registry, boolean frozen) {
        try {
            if (frozen) {
                registry.getClass().getMethod("freeze").invoke(registry);
            } else {
                Method unfreeze = registry.getClass().getMethod("unfreeze", boolean.class);
                unfreeze.invoke(registry, false);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to toggle the recipe-serializer registry frozen state", exception);
        }
    }
}
