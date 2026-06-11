package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trip test for {@link CombinerTransferPacket}, mirroring {@link com.smashingmods.alchemistry.common.network.SetRecipePacketTest}.
 * Its three fields (BlockPos + ItemStack + boolean) are private with no getters, so the round-trip is asserted two
 * ways: by re-reading the encoded buffer in the same field order, and by re-encoding the decoded packet and comparing
 * the bytes. The {@link ItemStack} field is written through {@link ItemStack#OPTIONAL_STREAM_CODEC}, which serializes
 * the item's registry id, so this extends {@link BootstrappedTest} (for a registry-backed buffer) and uses a vanilla
 * {@code Items.*} stack.
 *
 * <p>Also pins the packet's static transfer math, which is package-visible exactly so it can run without a live
 * server: {@link CombinerTransferPacket#buildCreativeTransfer} must keep the placement list index-parallel to the
 * recipe input when an ingredient resolves to no items (empty tag, custom ingredient resolving empty) and must
 * exclude those EMPTY placeholders from the operation count, and {@link CombinerTransferPacket#isFullMatch} must
 * only let the non-creative path transfer when every ingredient claimed a slot.</p>
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

    @Test
    void buildCreativeTransfer_zeroResolvingFirstInput_keepsSlotAlignment() {
        CombinerRecipe recipe = twoInputRecipe(zeroResolvingIngredient(1), new IngredientStack(Items.IRON_INGOT, 2));

        List<ItemStack> toPlace = CombinerTransferPacket.buildCreativeTransfer(recipe, false);

        // The unresolvable input stays an EMPTY placeholder at index 0 so the resolvable input
        // still targets its own slot (index 1) instead of shifting down into slot 0.
        assertEquals(2, toPlace.size());
        assertTrue(toPlace.get(0).isEmpty());
        assertTrue(toPlace.get(1).is(Items.IRON_INGOT));
        assertEquals(2, toPlace.get(1).getCount());
    }

    @Test
    void buildCreativeTransfer_excludesEmptyPlaceholdersFromOperationCount() {
        CombinerRecipe recipe = twoInputRecipe(zeroResolvingIngredient(1), new IngredientStack(Items.IRON_INGOT, 2));

        List<ItemStack> toPlace = CombinerTransferPacket.buildCreativeTransfer(recipe, true);

        // Max transfer fills to the stack limit: 64 / 2 per operation = 32 operations -> 64 items.
        // If the EMPTY placeholder leaked into getMaxOperations, its zero count would clamp the
        // operation count to zero and zero out every slot.
        assertEquals(64, toPlace.get(1).getCount());
    }

    @Test
    void buildCreativeTransfer_noInputResolves_returnsNoTransfer() {
        CombinerRecipe recipe = twoInputRecipe(zeroResolvingIngredient(1), zeroResolvingIngredient(2));

        assertTrue(CombinerTransferPacket.buildCreativeTransfer(recipe, true).isEmpty());
    }

    @Test
    void isFullMatch_everyIngredientClaimedASlot_transferable() {
        assertTrue(CombinerTransferPacket.isFullMatch(List.of(
                new TransferUtils.SlotMatch(new ItemStack(Items.IRON_INGOT, 4), 0),
                new TransferUtils.SlotMatch(new ItemStack(Items.GUNPOWDER, 8), 5))));
    }

    @Test
    void isFullMatch_partialMatch_notTransferable() {
        assertFalse(CombinerTransferPacket.isFullMatch(List.of(
                TransferUtils.SlotMatch.EMPTY,
                new TransferUtils.SlotMatch(new ItemStack(Items.GUNPOWDER, 8), 5))));
    }

    @Test
    void isFullMatch_emptyList_notTransferable() {
        assertFalse(CombinerTransferPacket.isFullMatch(List.of()));
    }

    /**
     * A two-input combiner recipe in declared order. The input set is a {@link LinkedHashSet}, matching how
     * {@link CombinerRecipe} stores it, so index 0/1 in {@code getInput()} are exactly the arguments' order.
     */
    private static CombinerRecipe twoInputRecipe(IngredientStack pFirst, IngredientStack pSecond) {
        return new CombinerRecipe(
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "test_combiner"),
                "",
                new LinkedHashSet<>(List.of(pFirst, pSecond)),
                new ItemStack(Items.OAK_SAPLING));
    }

    /**
     * An IngredientStack whose ingredient resolves to no items. The resolution surface is a NeoForge
     * custom ingredient with an empty item stream; at this version neither degenerate production
     * shape can reach an {@code IngredientStack} directly in a plain-JUnit JVM: the constructor
     * derives a registry name from {@code values[0]} of the wrapped {@code Ingredient}, which a
     * custom ingredient leaves empty (throwing AIOOBE), and an empty item tag never resolves empty
     * at all -- 1.21.1's {@code TagValue.getItems()} substitutes a BARRIER "Empty Tag" placeholder.
     * So the stack is built against a real item and {@code getIngredient()} overridden to hand out
     * the zero-resolving ingredient, which is the only surface {@code buildCreativeTransfer} reads.
     * {@code getType()} exists for codec round-trips, which these tests never perform.
     */
    private static IngredientStack zeroResolvingIngredient(int pCount) {
        Ingredient zeroResolving = new Ingredient(new ICustomIngredient() {
            @Override
            public boolean test(ItemStack pStack) {
                return false;
            }

            @Override
            public Stream<ItemStack> getItems() {
                return Stream.empty();
            }

            @Override
            public boolean isSimple() {
                return false;
            }

            @Override
            public IngredientType<?> getType() {
                throw new UnsupportedOperationException("Not serialized in tests");
            }
        });
        return new IngredientStack(Items.IRON_INGOT, pCount) {
            @Override
            public Ingredient getIngredient() {
                return zeroResolving;
            }
        };
    }

    private static byte[] readableBytes(RegistryFriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
