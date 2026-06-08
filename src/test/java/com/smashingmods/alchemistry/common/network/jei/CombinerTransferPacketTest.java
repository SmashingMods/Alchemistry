package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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
 * the bytes. The {@link ItemStack} field is written through {@link ItemStack#OPTIONAL_STREAM_CODEC}, which serializes
 * the item's registry id, so this extends {@link BootstrappedTest} (for a registry-backed buffer) and uses a vanilla
 * {@code Items.*} stack.
 */
class CombinerTransferPacketTest extends BootstrappedTest {

    @Test
    void combinerTransferPacket_roundTrip() {
        BlockPos pos = new BlockPos(12, -34, 56);
        ItemStack output = new ItemStack(Items.IRON_INGOT, 7);
        boolean maxTransfer = true;

        CombinerTransferPacket original = new CombinerTransferPacket(pos, output, maxTransfer);

        // Encode via the stream codec, then decode it back, then re-encode the decoded copy.
        RegistryFriendlyByteBuf encoded = registryBuffer();
        CombinerTransferPacket.STREAM_CODEC.encode(encoded, original);
        byte[] encodedBytes = readableBytes(encoded);

        // The encode order is blockPos, output, maxTransfer -- re-read each field with the codec the packet composes
        // to assert the field values survive the trip.
        RegistryFriendlyByteBuf forFields = registryBuffer();
        forFields.writeBytes(encodedBytes);
        assertEquals(pos, BlockPos.STREAM_CODEC.decode(forFields));
        ItemStack decodedOutput = ItemStack.OPTIONAL_STREAM_CODEC.decode(forFields);
        assertTrue(ItemStack.isSameItemSameComponents(output, decodedOutput));
        assertEquals(output.getCount(), decodedOutput.getCount());
        assertEquals(maxTransfer, ByteBufCodecs.BOOL.decode(forFields));

        RegistryFriendlyByteBuf forDecode = registryBuffer();
        forDecode.writeBytes(encodedBytes);
        CombinerTransferPacket decoded = CombinerTransferPacket.STREAM_CODEC.decode(forDecode);

        RegistryFriendlyByteBuf reEncoded = registryBuffer();
        CombinerTransferPacket.STREAM_CODEC.encode(reEncoded, decoded);

        assertArrayEquals(encodedBytes, readableBytes(reEncoded));
    }

    private static byte[] readableBytes(RegistryFriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
