package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins {@link TransferUtils#matchIngredientListToItemStack}'s joint parallel-list contract: the
 * result has one entry per ingredient, in ingredient order, with {@link TransferUtils.SlotMatch#EMPTY}
 * at every position the inventory cannot satisfy, and a slot's count is consumed as ingredients
 * claim it, so two ingredients satisfiable by the same stack only both match while the stack
 * covers both. {@link CombinerTransferPacket}'s non-creative path index-pairs the result with the
 * recipe input and removes by the carried slot, so a miss that was skipped instead of kept as
 * EMPTY would re-pair every later count with the wrong ingredient, and a per-ingredient match
 * would let the removal loop run a shared stack dry part-way through. Also pins the joint
 * {@link TransferUtils#getMaxOperations(List, List, boolean)} bound: a shared slot divides its
 * count by the TOTAL claim on it, not by each claim separately, and the shared
 * {@link TransferUtils#isFullMatch} gate, which must only let a non-creative transfer run when
 * every ingredient claimed a slot.
 * Extends {@link BootstrappedTest} because building and testing {@code Ingredient}s resolves item
 * holders from the built-in registries.
 */
class TransferUtilsTest extends BootstrappedTest {

    @Test
    void matchIngredientList_fullInventoryInAnyOrder_pairsEachIngredientByIndex() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(0, new ItemStack(Items.GUNPOWDER, 8));
        inventory.set(3, new ItemStack(Items.IRON_INGOT, 4));

        List<IngredientStack> ingredients = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.GUNPOWDER, 3));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // Each index pairs with its own ingredient's matching stack and slot even though the
        // inventory holds the items in the opposite order from the recipe.
        assertEquals(2, matched.size());
        assertTrue(matched.get(0).itemStack().is(Items.IRON_INGOT));
        assertEquals(4, matched.get(0).itemStack().getCount());
        assertEquals(3, matched.get(0).slot());
        assertTrue(matched.get(1).itemStack().is(Items.GUNPOWDER));
        assertEquals(8, matched.get(1).itemStack().getCount());
        assertEquals(0, matched.get(1).slot());
    }

    @Test
    void matchIngredientList_partialInventory_keepsEmptyPlaceholderAtUnmatchedIndex() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.GUNPOWDER, 8));

        List<IngredientStack> ingredients = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.GUNPOWDER, 3));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        assertEquals(2, matched.size());
        assertTrue(matched.get(0).isEmpty());
        assertTrue(matched.get(1).itemStack().is(Items.GUNPOWDER));
    }

    @Test
    void matchIngredientList_matchingItemButShortCount_staysEmpty() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(1, new ItemStack(Items.IRON_INGOT, 1));

        List<IngredientStack> ingredients = List.of(new IngredientStack(Items.IRON_INGOT, 2));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        assertEquals(1, matched.size());
        assertTrue(matched.get(0).isEmpty());
    }

    @Test
    void matchIngredientList_sharedStackCoveringBothClaims_matchesBothToSameSlot() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.IRON_INGOT, 5));

        List<IngredientStack> ingredients = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.IRON_INGOT, 3));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // 2 + 3 fit in the 5-stack, so both ingredients claim slot 2.
        assertEquals(2, matched.get(0).slot());
        assertEquals(2, matched.get(1).slot());
    }

    @Test
    void matchIngredientList_sharedStackShortForSecondClaim_secondStaysEmpty() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.IRON_INGOT, 4));

        List<IngredientStack> ingredients = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.IRON_INGOT, 3));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // The first claim consumes 2 of the 4; the remaining 2 cannot cover the second claim's 3.
        // A per-ingredient match would have said yes twice (4 >= 2, 4 >= 3) and authorized
        // removing 5 items from a stack of 4.
        assertEquals(2, matched.get(0).slot());
        assertTrue(matched.get(1).isEmpty());
    }

    @Test
    void matchIngredientList_consumedSlot_overflowsToNextMatchingSlot() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(1, new ItemStack(Items.IRON_INGOT, 2));
        inventory.set(4, new ItemStack(Items.IRON_INGOT, 3));

        List<IngredientStack> ingredients = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.IRON_INGOT, 3));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // The first claim drains slot 1, so the second claim moves on to slot 4 instead of failing.
        assertEquals(1, matched.get(0).slot());
        assertEquals(4, matched.get(1).slot());
    }

    @Test
    void getMaxOperations_sharedSlotMaxTransfer_boundsBySummedClaims() {
        ItemStack shared = new ItemStack(Items.IRON_INGOT, 64);
        List<TransferUtils.SlotMatch> matches = List.of(
                new TransferUtils.SlotMatch(shared, 2),
                new TransferUtils.SlotMatch(shared, 2));
        List<IngredientStack> recipeInput = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.IRON_INGOT, 3));

        // Each operation draws 2 + 3 = 5 from the shared stack: 64 / 5 = 12 operations. The old
        // per-ingredient bound min(64/2, 64/3) = 21 would have placed 105 items against the 64
        // the stack could actually surrender.
        assertEquals(12, TransferUtils.getMaxOperations(matches, recipeInput, true));
    }

    @Test
    void getMaxOperations_separateSlotsMaxTransfer_keepsPerIngredientBound() {
        List<TransferUtils.SlotMatch> matches = List.of(
                new TransferUtils.SlotMatch(new ItemStack(Items.IRON_INGOT, 64), 0),
                new TransferUtils.SlotMatch(new ItemStack(Items.GUNPOWDER, 9), 3));
        List<IngredientStack> recipeInput = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.GUNPOWDER, 3));

        // Unshared slots keep the plain stackCount / recipeCount minimum: min(64/2, 9/3) = 3.
        assertEquals(3, TransferUtils.getMaxOperations(matches, recipeInput, true));
    }

    @Test
    void getMaxOperations_withoutMaxTransfer_isOneOperation() {
        List<TransferUtils.SlotMatch> matches = List.of(new TransferUtils.SlotMatch(new ItemStack(Items.IRON_INGOT, 64), 0));
        List<IngredientStack> recipeInput = List.of(new IngredientStack(Items.IRON_INGOT, 2));

        assertEquals(1, TransferUtils.getMaxOperations(matches, recipeInput, false));
    }

    @Test
    void isFullMatch_everyIngredientClaimedASlot_transferable() {
        assertTrue(TransferUtils.isFullMatch(List.of(
                new TransferUtils.SlotMatch(new ItemStack(Items.IRON_INGOT, 4), 0),
                new TransferUtils.SlotMatch(new ItemStack(Items.GUNPOWDER, 8), 5))));
    }

    @Test
    void isFullMatch_partialMatch_notTransferable() {
        assertFalse(TransferUtils.isFullMatch(List.of(
                TransferUtils.SlotMatch.EMPTY,
                new TransferUtils.SlotMatch(new ItemStack(Items.GUNPOWDER, 8), 5))));
    }

    @Test
    void isFullMatch_emptyList_notTransferable() {
        assertFalse(TransferUtils.isFullMatch(List.of()));
    }
}
