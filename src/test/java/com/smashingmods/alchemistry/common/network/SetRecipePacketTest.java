package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-1 round-trip test for {@link SetRecipePacket}. Its three fields are private with no getters, so the round-trip
 * is asserted two ways: by re-reading the encoded buffer in the same field order (direct field values), and by
 * re-encoding the decoded packet and comparing the bytes (faithful end-to-end equivalence).
 *
 * <p>Serialization runs through {@link SetRecipePacket#STREAM_CODEC} over a {@link RegistryFriendlyByteBuf}, the
 * registry-aware buffer the codec is bound to on the network.</p>
 */
class SetRecipePacketTest extends BootstrappedTest {

    @Test
    void setRecipePacket_roundTrip() {
        BlockPos pos = new BlockPos(12, -34, 56);
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath("alchemistry", "fusion/iron");
        String group = "alchemistry:fusion";

        SetRecipePacket original = new SetRecipePacket(pos, recipeId, group);

        // Encode via the stream codec, then decode it back, then re-encode the decoded copy.
        RegistryFriendlyByteBuf encoded = registryBuffer();
        SetRecipePacket.STREAM_CODEC.encode(encoded, original);
        byte[] encodedBytes = readableBytes(encoded);

        // The encode order is blockPos, recipeId, group -- re-read it to assert the field values survive the trip.
        RegistryFriendlyByteBuf forFields = registryBuffer();
        forFields.writeBytes(encodedBytes);
        assertEquals(pos, forFields.readBlockPos());
        assertEquals(recipeId, forFields.readResourceLocation());
        assertEquals(group, forFields.readUtf());

        RegistryFriendlyByteBuf forDecode = registryBuffer();
        forDecode.writeBytes(encodedBytes);
        SetRecipePacket decoded = SetRecipePacket.STREAM_CODEC.decode(forDecode);

        RegistryFriendlyByteBuf reEncoded = registryBuffer();
        SetRecipePacket.STREAM_CODEC.encode(reEncoded, decoded);

        assertArrayEquals(encodedBytes, readableBytes(reEncoded));
    }

    private static byte[] readableBytes(RegistryFriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
