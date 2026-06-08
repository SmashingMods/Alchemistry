package com.smashingmods.alchemistry.common.recipe;

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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Round-trip contracts for the seven recipe serializers' {@code streamCodec()}s. The 1.20.6 recipe API replaced the
 * old {@code toNetwork}/{@code fromNetwork} pair with a {@code StreamCodec} returned from
 * {@link net.minecraft.world.item.crafting.RecipeSerializer#streamCodec()}, so an encode/decode round trip per
 * serializer is a contract surface that did not exist before.
 *
 * <p>Each serializer is constructed exactly as the registry binds it ({@code new XSerializer<>(XRecipe::new)}); no
 * registration is needed because the codec is a plain field on the serializer object. A freshly decoded recipe
 * carries {@link AlchemistryRecipeCodecs#UNKEYED_RECIPE_ID} (recipe identity lives on the {@code RecipeHolder}, not
 * the wire payload), so the recipes here are built with that same id and the round trip is asserted by re-encoding
 * the decoded recipe and comparing the bytes. To keep that non-vacuous, each recipe is paired with a variant that
 * differs in exactly one field, and the two must encode differently -- a serializer that dropped a field would
 * collide. The Fission output pair, the Fusion input pair, and the dissolver {@link ProbabilitySet} shape are
 * additionally pinned on the decoded recipe so the round trip preserves their cardinality, not just their bytes.
 * Item/fluid content encodes by registry id, so this extends {@link BootstrappedTest} for a registry-backed
 * buffer and uses vanilla {@code Items.*}/{@code Fluids.*} content.</p>
 */
class RecipeSerializerStreamCodecRoundTripTest extends BootstrappedTest {

    private static final ResourceLocation ID = AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID;
    private static final String GROUP = "test";

    @Test
    void atomizerSerializer_roundTripsAndSerializesEachField() {
        StreamCodec<RegistryFriendlyByteBuf, AtomizerRecipe> codec = new AtomizerRecipeSerializer<>(AtomizerRecipe::new).streamCodec();

        AtomizerRecipe recipe = new AtomizerRecipe(ID, GROUP, new FluidStack(Fluids.WATER, 500), new ItemStack(Items.SNOWBALL));
        assertRoundTrips(codec, recipe);
        // input fluid differs
        assertEncodingsDiffer(codec, recipe,
                new AtomizerRecipe(ID, GROUP, new FluidStack(Fluids.LAVA, 500), new ItemStack(Items.SNOWBALL)));
        // output item differs
        assertEncodingsDiffer(codec, recipe,
                new AtomizerRecipe(ID, GROUP, new FluidStack(Fluids.WATER, 500), new ItemStack(Items.CLAY_BALL)));
    }

    @Test
    void compactorSerializer_roundTripsAndSerializesEachField() {
        StreamCodec<RegistryFriendlyByteBuf, CompactorRecipe> codec = new CompactorRecipeSerializer<>(CompactorRecipe::new).streamCodec();

        CompactorRecipe recipe = new CompactorRecipe(ID, GROUP, new IngredientStack(Items.IRON_INGOT), new ItemStack(Items.IRON_BLOCK));
        assertRoundTrips(codec, recipe);
        // input ingredient differs
        assertEncodingsDiffer(codec, recipe,
                new CompactorRecipe(ID, GROUP, new IngredientStack(Items.GOLD_INGOT), new ItemStack(Items.IRON_BLOCK)));
        // output item differs
        assertEncodingsDiffer(codec, recipe,
                new CompactorRecipe(ID, GROUP, new IngredientStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_BLOCK)));
    }

    @Test
    void combinerSerializer_roundTripsAndSerializesEachField() {
        StreamCodec<RegistryFriendlyByteBuf, CombinerRecipe> codec = new CombinerRecipeSerializer<>(CombinerRecipe::new).streamCodec();

        CombinerRecipe recipe = new CombinerRecipe(ID, GROUP, inputSet(Items.IRON_INGOT, Items.GOLD_INGOT), new ItemStack(Items.NETHERITE_INGOT));
        assertRoundTrips(codec, recipe);
        // input set differs
        assertEncodingsDiffer(codec, recipe,
                new CombinerRecipe(ID, GROUP, inputSet(Items.IRON_INGOT, Items.COPPER_INGOT), new ItemStack(Items.NETHERITE_INGOT)));
        // output item differs
        assertEncodingsDiffer(codec, recipe,
                new CombinerRecipe(ID, GROUP, inputSet(Items.IRON_INGOT, Items.GOLD_INGOT), new ItemStack(Items.DIAMOND)));
    }

    @Test
    void liquifierSerializer_roundTripsAndSerializesEachField() {
        StreamCodec<RegistryFriendlyByteBuf, LiquifierRecipe> codec = new LiquifierRecipeSerializer<>(LiquifierRecipe::new).streamCodec();

        LiquifierRecipe recipe = new LiquifierRecipe(ID, GROUP, new IngredientStack(Items.ICE), new FluidStack(Fluids.WATER, 500));
        assertRoundTrips(codec, recipe);
        // input ingredient differs
        assertEncodingsDiffer(codec, recipe,
                new LiquifierRecipe(ID, GROUP, new IngredientStack(Items.PACKED_ICE), new FluidStack(Fluids.WATER, 500)));
        // output fluid differs
        assertEncodingsDiffer(codec, recipe,
                new LiquifierRecipe(ID, GROUP, new IngredientStack(Items.ICE), new FluidStack(Fluids.LAVA, 500)));
    }

    @Test
    void fissionSerializer_roundTripsAndPinsTwoOutputs() {
        StreamCodec<RegistryFriendlyByteBuf, FissionRecipe> codec = new FissionRecipeSerializer<>(FissionRecipe::new).streamCodec();

        FissionRecipe recipe = new FissionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.COPPER_INGOT));
        assertRoundTrips(codec, recipe);

        // Fission carries one input and exactly two outputs; the decoded recipe must preserve that shape.
        FissionRecipe decoded = decode(codec, recipe);
        assertEquals(2, ((java.util.List<?>) decoded.getOutput()).size());
        assertEquals(Items.IRON_INGOT, decoded.getInput().getItem());
        assertEquals(Items.GOLD_INGOT, decoded.getOutput1().getItem());
        assertEquals(Items.COPPER_INGOT, decoded.getOutput2().getItem());

        // input field differs
        assertEncodingsDiffer(codec, recipe, new FissionRecipe(ID, GROUP,
                new ItemStack(Items.NETHERITE_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.COPPER_INGOT)));
        // output1 field differs
        assertEncodingsDiffer(codec, recipe, new FissionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(Items.COPPER_INGOT)));
        // output2 field differs
        assertEncodingsDiffer(codec, recipe, new FissionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DIAMOND)));
    }

    @Test
    void fusionSerializer_roundTripsAndPinsTwoInputs() {
        StreamCodec<RegistryFriendlyByteBuf, FusionRecipe> codec = new FusionRecipeSerializer<>(FusionRecipe::new).streamCodec();

        FusionRecipe recipe = new FusionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.NETHERITE_INGOT));
        assertRoundTrips(codec, recipe);

        // Fusion carries exactly two inputs and one output; the decoded recipe must preserve that shape.
        FusionRecipe decoded = decode(codec, recipe);
        assertEquals(2, ((java.util.List<?>) decoded.getInput()).size());
        assertEquals(Items.IRON_INGOT, decoded.getInput1().getItem());
        assertEquals(Items.GOLD_INGOT, decoded.getInput2().getItem());
        assertEquals(Items.NETHERITE_INGOT, decoded.getOutput().getItem());

        // input1 field differs
        assertEncodingsDiffer(codec, recipe, new FusionRecipe(ID, GROUP,
                new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.NETHERITE_INGOT)));
        // input2 field differs
        assertEncodingsDiffer(codec, recipe, new FusionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.NETHERITE_INGOT)));
        // output field differs
        assertEncodingsDiffer(codec, recipe, new FusionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DIAMOND)));
    }

    @Test
    void dissolverSerializer_roundTripsAndPinsProbabilitySetShape() {
        StreamCodec<RegistryFriendlyByteBuf, DissolverRecipe> codec = new DissolverRecipeSerializer<>(DissolverRecipe::new).streamCodec();

        ProbabilitySet output = ProbabilitySet.Builder.createSet()
                .weighted()
                .addGroup(60.0, new ItemStack(Items.IRON_NUGGET))
                .addGroup(40.0, new ItemStack(Items.GOLD_NUGGET))
                .build();
        DissolverRecipe recipe = new DissolverRecipe(ID, GROUP, new IngredientStack(Items.IRON_INGOT), output);
        assertRoundTrips(codec, recipe);

        // The dissolver output is a ProbabilitySet whose group list, weighted flag and rolls count must survive the
        // round trip -- the shape the StreamCodec composes group-by-group.
        DissolverRecipe decoded = decode(codec, recipe);
        ProbabilitySet decodedSet = decoded.getOutput();
        assertEquals(output.isWeighted(), decodedSet.isWeighted());
        assertEquals(output.getRolls(), decodedSet.getRolls());
        assertEquals(output.getProbabilityGroups().size(), decodedSet.getProbabilityGroups().size());
        ProbabilityGroup firstGroup = decodedSet.getProbabilityGroups().get(0);
        assertEquals(60.0, firstGroup.getProbability());
        assertEquals(Items.IRON_NUGGET, firstGroup.getOutput().get(0).getItem());

        // input ingredient differs
        assertEncodingsDiffer(codec, recipe,
                new DissolverRecipe(ID, GROUP, new IngredientStack(Items.GOLD_INGOT), output));
        // output ProbabilitySet differs (a different result item in the first group)
        ProbabilitySet otherOutput = ProbabilitySet.Builder.createSet()
                .weighted()
                .addGroup(60.0, new ItemStack(Items.DIAMOND))
                .addGroup(40.0, new ItemStack(Items.GOLD_NUGGET))
                .build();
        assertEncodingsDiffer(codec, recipe,
                new DissolverRecipe(ID, GROUP, new IngredientStack(Items.IRON_INGOT), otherOutput));
    }

    private static Set<IngredientStack> inputSet(net.minecraft.world.item.Item... items) {
        Set<IngredientStack> inputs = new LinkedHashSet<>();
        for (net.minecraft.world.item.Item item : items) {
            inputs.add(new IngredientStack(item));
        }
        return inputs;
    }

    private static <T> void assertRoundTrips(StreamCodec<RegistryFriendlyByteBuf, T> codec, T recipe) {
        byte[] encoded = encode(codec, recipe);
        assertArrayEquals(encoded, encode(codec, decode(codec, recipe)));
    }

    private static <T> void assertEncodingsDiffer(StreamCodec<RegistryFriendlyByteBuf, T> codec, T first, T second) {
        assertFalse(Arrays.equals(encode(codec, first), encode(codec, second)),
                "recipes that differ in one field must not encode identically");
    }

    private static <T> T decode(StreamCodec<RegistryFriendlyByteBuf, T> codec, T recipe) {
        RegistryFriendlyByteBuf buffer = registryBuffer();
        buffer.writeBytes(encode(codec, recipe));
        return codec.decode(buffer);
    }

    private static <T> byte[] encode(StreamCodec<RegistryFriendlyByteBuf, T> codec, T recipe) {
        RegistryFriendlyByteBuf buffer = registryBuffer();
        codec.encode(buffer, recipe);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
