package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.network.jei.CombinerTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.CompactorTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.DissolverTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FissionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FusionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.LiquifierTransferPacket;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Round-trip contracts for all ten Alchemistry packets' {@link StreamCodec}s. The 1.20.6 payload API moved each
 * packet's serialization out of an imperative {@code write} method and into a {@code STREAM_CODEC} field, so an
 * encode/decode round trip per packet is a contract surface that did not exist before.
 *
 * <p>Every packet's fields are private with no getters, so the round trip is asserted by re-encoding the decoded
 * packet and comparing the bytes against the original encoding. To keep that non-vacuous -- a codec that dropped a
 * field would still round-trip the bytes it does write -- each packet is also paired with a variant that differs in
 * exactly one field, and the two must encode to different byte arrays. That proves the codec actually serializes
 * each field: were a composite component paired with the wrong field accessor, the differing-field variant would
 * collide with the original and the distinctness assertion would fail. The {@code ItemStack}/{@code FluidStack}/
 * {@code IngredientStack} fields encode by registry id, so this extends {@link BootstrappedTest} for a
 * registry-backed buffer and uses vanilla {@code Items.*} content.</p>
 */
class PacketStreamCodecRoundTripTest extends BootstrappedTest {

    private static final BlockPos POS = new BlockPos(12, -34, 56);
    private static final BlockPos OTHER_POS = new BlockPos(-7, 8, 9);
    private static final ItemStack STACK = new ItemStack(Items.IRON_INGOT, 7);
    private static final ItemStack OTHER_STACK = new ItemStack(Items.GOLD_INGOT, 7);

    @Test
    void setRecipePacket_roundTripsAndSerializesEachField() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("alchemistry", "fusion/iron");
        ResourceLocation otherId = ResourceLocation.fromNamespaceAndPath("alchemistry", "fusion/gold");

        assertRoundTrips(SetRecipePacket.STREAM_CODEC, new SetRecipePacket(POS, id, "g"));
        // blockPos field differs
        assertEncodingsDiffer(SetRecipePacket.STREAM_CODEC,
                new SetRecipePacket(POS, id, "g"), new SetRecipePacket(OTHER_POS, id, "g"));
        // recipeId field differs
        assertEncodingsDiffer(SetRecipePacket.STREAM_CODEC,
                new SetRecipePacket(POS, id, "g"), new SetRecipePacket(POS, otherId, "g"));
        // group field differs
        assertEncodingsDiffer(SetRecipePacket.STREAM_CODEC,
                new SetRecipePacket(POS, id, "g"), new SetRecipePacket(POS, id, "h"));
    }

    @Test
    void setSideConfigurationPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(SetSideConfigurationPacket.STREAM_CODEC, new SetSideConfigurationPacket(POS, (short) 42));
        // blockPos field differs
        assertEncodingsDiffer(SetSideConfigurationPacket.STREAM_CODEC,
                new SetSideConfigurationPacket(POS, (short) 42), new SetSideConfigurationPacket(OTHER_POS, (short) 42));
        // sideConfigurationBits field differs
        assertEncodingsDiffer(SetSideConfigurationPacket.STREAM_CODEC,
                new SetSideConfigurationPacket(POS, (short) 42), new SetSideConfigurationPacket(POS, (short) 7));
    }

    @Test
    void toggleAutoBalanceButtonPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(ToggleAutoBalanceButtonPacket.STREAM_CODEC, new ToggleAutoBalanceButtonPacket(POS, true));
        // blockPos field differs
        assertEncodingsDiffer(ToggleAutoBalanceButtonPacket.STREAM_CODEC,
                new ToggleAutoBalanceButtonPacket(POS, true), new ToggleAutoBalanceButtonPacket(OTHER_POS, true));
        // autoBalance field differs
        assertEncodingsDiffer(ToggleAutoBalanceButtonPacket.STREAM_CODEC,
                new ToggleAutoBalanceButtonPacket(POS, true), new ToggleAutoBalanceButtonPacket(POS, false));
    }

    @Test
    void toggleReactorAutoejectPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(ToggleReactorAutoejectPacket.STREAM_CODEC, new ToggleReactorAutoejectPacket(POS, true));
        // blockPos field differs
        assertEncodingsDiffer(ToggleReactorAutoejectPacket.STREAM_CODEC,
                new ToggleReactorAutoejectPacket(POS, true), new ToggleReactorAutoejectPacket(OTHER_POS, true));
        // autoeject field differs
        assertEncodingsDiffer(ToggleReactorAutoejectPacket.STREAM_CODEC,
                new ToggleReactorAutoejectPacket(POS, true), new ToggleReactorAutoejectPacket(POS, false));
    }

    @Test
    void combinerTransferPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(CombinerTransferPacket.STREAM_CODEC, new CombinerTransferPacket(POS, STACK, true));
        // blockPos field differs
        assertEncodingsDiffer(CombinerTransferPacket.STREAM_CODEC,
                new CombinerTransferPacket(POS, STACK, true), new CombinerTransferPacket(OTHER_POS, STACK, true));
        // output field differs
        assertEncodingsDiffer(CombinerTransferPacket.STREAM_CODEC,
                new CombinerTransferPacket(POS, STACK, true), new CombinerTransferPacket(POS, OTHER_STACK, true));
        // maxTransfer field differs
        assertEncodingsDiffer(CombinerTransferPacket.STREAM_CODEC,
                new CombinerTransferPacket(POS, STACK, true), new CombinerTransferPacket(POS, STACK, false));
    }

    @Test
    void compactorTransferPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(CompactorTransferPacket.STREAM_CODEC, new CompactorTransferPacket(POS, STACK, true));
        // blockPos field differs
        assertEncodingsDiffer(CompactorTransferPacket.STREAM_CODEC,
                new CompactorTransferPacket(POS, STACK, true), new CompactorTransferPacket(OTHER_POS, STACK, true));
        // output field differs
        assertEncodingsDiffer(CompactorTransferPacket.STREAM_CODEC,
                new CompactorTransferPacket(POS, STACK, true), new CompactorTransferPacket(POS, OTHER_STACK, true));
        // maxTransfer field differs
        assertEncodingsDiffer(CompactorTransferPacket.STREAM_CODEC,
                new CompactorTransferPacket(POS, STACK, true), new CompactorTransferPacket(POS, STACK, false));
    }

    @Test
    void fissionTransferPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(FissionTransferPacket.STREAM_CODEC, new FissionTransferPacket(POS, STACK, true));
        // blockPos field differs
        assertEncodingsDiffer(FissionTransferPacket.STREAM_CODEC,
                new FissionTransferPacket(POS, STACK, true), new FissionTransferPacket(OTHER_POS, STACK, true));
        // input field differs
        assertEncodingsDiffer(FissionTransferPacket.STREAM_CODEC,
                new FissionTransferPacket(POS, STACK, true), new FissionTransferPacket(POS, OTHER_STACK, true));
        // maxTransfer field differs
        assertEncodingsDiffer(FissionTransferPacket.STREAM_CODEC,
                new FissionTransferPacket(POS, STACK, true), new FissionTransferPacket(POS, STACK, false));
    }

    @Test
    void fusionTransferPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(FusionTransferPacket.STREAM_CODEC, new FusionTransferPacket(POS, STACK, OTHER_STACK, true));
        // blockPos field differs
        assertEncodingsDiffer(FusionTransferPacket.STREAM_CODEC,
                new FusionTransferPacket(POS, STACK, OTHER_STACK, true), new FusionTransferPacket(OTHER_POS, STACK, OTHER_STACK, true));
        // input1 field differs
        assertEncodingsDiffer(FusionTransferPacket.STREAM_CODEC,
                new FusionTransferPacket(POS, STACK, OTHER_STACK, true), new FusionTransferPacket(POS, OTHER_STACK, OTHER_STACK, true));
        // input2 field differs
        assertEncodingsDiffer(FusionTransferPacket.STREAM_CODEC,
                new FusionTransferPacket(POS, STACK, OTHER_STACK, true), new FusionTransferPacket(POS, STACK, STACK, true));
        // maxTransfer field differs
        assertEncodingsDiffer(FusionTransferPacket.STREAM_CODEC,
                new FusionTransferPacket(POS, STACK, OTHER_STACK, true), new FusionTransferPacket(POS, STACK, OTHER_STACK, false));
    }

    @Test
    void dissolverTransferPacket_roundTripsAndSerializesEachField() {
        IngredientStack input = new IngredientStack(Items.IRON_INGOT, 3);
        IngredientStack otherInput = new IngredientStack(Items.GOLD_INGOT, 3);

        assertRoundTrips(DissolverTransferPacket.STREAM_CODEC, new DissolverTransferPacket(POS, input, true));
        // blockPos field differs
        assertEncodingsDiffer(DissolverTransferPacket.STREAM_CODEC,
                new DissolverTransferPacket(POS, input, true), new DissolverTransferPacket(OTHER_POS, input, true));
        // input field differs
        assertEncodingsDiffer(DissolverTransferPacket.STREAM_CODEC,
                new DissolverTransferPacket(POS, input, true), new DissolverTransferPacket(POS, otherInput, true));
        // maxTransfer field differs
        assertEncodingsDiffer(DissolverTransferPacket.STREAM_CODEC,
                new DissolverTransferPacket(POS, input, true), new DissolverTransferPacket(POS, input, false));
    }

    @Test
    void liquifierTransferPacket_roundTripsAndSerializesEachField() {
        IngredientStack input = new IngredientStack(Items.IRON_INGOT, 3);
        IngredientStack otherInput = new IngredientStack(Items.GOLD_INGOT, 3);

        assertRoundTrips(LiquifierTransferPacket.STREAM_CODEC, new LiquifierTransferPacket(POS, input, true));
        // blockPos field differs
        assertEncodingsDiffer(LiquifierTransferPacket.STREAM_CODEC,
                new LiquifierTransferPacket(POS, input, true), new LiquifierTransferPacket(OTHER_POS, input, true));
        // input field differs
        assertEncodingsDiffer(LiquifierTransferPacket.STREAM_CODEC,
                new LiquifierTransferPacket(POS, input, true), new LiquifierTransferPacket(POS, otherInput, true));
        // maxTransfer field differs
        assertEncodingsDiffer(LiquifierTransferPacket.STREAM_CODEC,
                new LiquifierTransferPacket(POS, input, true), new LiquifierTransferPacket(POS, input, false));
    }

    private static <T> void assertRoundTrips(StreamCodec<RegistryFriendlyByteBuf, T> codec, T packet) {
        byte[] encoded = encode(codec, packet);

        RegistryFriendlyByteBuf forDecode = registryBuffer();
        forDecode.writeBytes(encoded);
        T decoded = codec.decode(forDecode);

        assertArrayEquals(encoded, encode(codec, decoded));
    }

    private static <T> void assertEncodingsDiffer(StreamCodec<RegistryFriendlyByteBuf, T> codec, T first, T second) {
        assertFalse(Arrays.equals(encode(codec, first), encode(codec, second)),
                "packets that differ in one field must not encode identically");
    }

    private static <T> byte[] encode(StreamCodec<RegistryFriendlyByteBuf, T> codec, T packet) {
        RegistryFriendlyByteBuf buffer = registryBuffer();
        codec.encode(buffer, packet);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
