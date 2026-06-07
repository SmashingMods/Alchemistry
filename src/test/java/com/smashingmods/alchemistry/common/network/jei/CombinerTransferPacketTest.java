package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trip test for {@link CombinerTransferPacket}, mirroring {@link com.smashingmods.alchemistry.common.network.SetRecipePacketTest}.
 * Its three fields (BlockPos + ItemStack + boolean) are private with no getters, so the round-trip is asserted two
 * ways: by re-reading the encoded buffer in the same field order, and by re-encoding the decoded packet and comparing
 * the bytes. The {@link ItemStack} field is written through {@code writeItem}, which serializes the item's registry id,
 * so this extends {@link BootstrappedTest} and uses a vanilla {@code Items.*} stack.
 */
class CombinerTransferPacketTest extends BootstrappedTest {

    @Test
    void combinerTransferPacket_roundTrip() {
        BlockPos pos = new BlockPos(12, -34, 56);
        ItemStack output = new ItemStack(Items.IRON_INGOT, 7);
        boolean maxTransfer = true;

        CombinerTransferPacket original = new CombinerTransferPacket(pos, output, maxTransfer);

        // Encode, then decode via the buffer constructor, then re-encode the decoded copy.
        FriendlyByteBuf encoded = new FriendlyByteBuf(Unpooled.buffer());
        original.write(encoded);
        byte[] encodedBytes = readableBytes(encoded);

        // The encode order is blockPos, output, maxTransfer -- re-read it to assert the field values survive the trip.
        FriendlyByteBuf forFields = new FriendlyByteBuf(Unpooled.wrappedBuffer(encodedBytes));
        assertEquals(pos, forFields.readBlockPos());
        ItemStack decodedOutput = forFields.readItem();
        assertTrue(ItemStack.isSameItemSameTags(output, decodedOutput));
        assertEquals(output.getCount(), decodedOutput.getCount());
        assertEquals(maxTransfer, forFields.readBoolean());

        CombinerTransferPacket decoded = new CombinerTransferPacket(new FriendlyByteBuf(Unpooled.wrappedBuffer(encodedBytes)));
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
