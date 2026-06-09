package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Round-trip contracts for the Alchemistry packets' {@link StreamCodec}s. The 1.20.6 payload API moved each
 * packet's serialization out of an imperative {@code write} method and into a {@code STREAM_CODEC} field, so an
 * encode/decode round trip per packet is a contract surface that did not exist before. The six JEI recipe-transfer
 * packets are not covered here this hop: JEI has no 1.21.3 build, so {@code common/network/jei/} is excluded from
 * compilation (see build.gradle) and their round-trip cases were dropped with it; they return when JEI does.
 *
 * <p>Every packet's fields are private with no getters, so the round trip is asserted by re-encoding the decoded
 * packet and comparing the bytes against the original encoding. To keep that non-vacuous -- a codec that dropped a
 * field would still round-trip the bytes it does write -- each packet is also paired with a variant that differs in
 * exactly one field, and the two must encode to different byte arrays. That proves the codec actually serializes
 * each field: were a composite component paired with the wrong field accessor, the differing-field variant would
 * collide with the original and the distinctness assertion would fail. {@code SetRecipePacket} encodes a
 * {@code ResourceLocation} by registry id, so this extends {@link BootstrappedTest} for a registry-backed buffer.</p>
 */
class PacketStreamCodecRoundTripTest extends BootstrappedTest {

    private static final BlockPos POS = new BlockPos(12, -34, 56);
    private static final BlockPos OTHER_POS = new BlockPos(-7, 8, 9);

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
