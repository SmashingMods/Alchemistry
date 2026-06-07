package com.smashingmods.alchemistry.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-1 round-trip test for {@link SetRecipePacket}. Its three fields are private with no getters, so the round-trip
 * is asserted two ways: by re-reading the encoded buffer in the same field order (direct field values), and by
 * re-encoding the decoded packet and comparing the bytes (faithful end-to-end equivalence).
 */
class SetRecipePacketTest {

    @Test
    void setRecipePacket_roundTrip() {
        BlockPos pos = new BlockPos(12, -34, 56);
        ResourceLocation recipeId = new ResourceLocation("alchemistry", "fusion/iron");
        String group = "alchemistry:fusion";

        SetRecipePacket original = new SetRecipePacket(pos, recipeId, group);

        // Encode, then decode via the buffer constructor, then re-encode the decoded copy.
        FriendlyByteBuf encoded = new FriendlyByteBuf(Unpooled.buffer());
        original.write(encoded);
        byte[] encodedBytes = readableBytes(encoded);

        // The encode order is blockPos, recipeId, group -- re-read it to assert the field values survive the trip.
        FriendlyByteBuf forFields = new FriendlyByteBuf(Unpooled.wrappedBuffer(encodedBytes));
        assertEquals(pos, forFields.readBlockPos());
        assertEquals(recipeId, forFields.readResourceLocation());
        assertEquals(group, forFields.readUtf());

        SetRecipePacket decoded = new SetRecipePacket(new FriendlyByteBuf(Unpooled.wrappedBuffer(encodedBytes)));
        FriendlyByteBuf reEncoded = new FriendlyByteBuf(Unpooled.buffer());
        decoded.write(reEncoded);

        assertArrayEquals(encodedBytes, readableBytes(reEncoded));
    }

    private static byte[] readableBytes(FriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
