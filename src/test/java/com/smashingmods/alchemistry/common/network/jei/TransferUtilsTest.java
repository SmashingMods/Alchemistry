package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins {@link TransferUtils#matchIngredientListToItemStack}'s parallel-list contract: the result has
 * one entry per ingredient, in ingredient order, with {@link ItemStack#EMPTY} at every position the
 * inventory cannot satisfy. {@link CombinerTransferPacket}'s non-creative path index-pairs the result
 * with the recipe input, so a miss that was skipped instead of kept as EMPTY would re-pair every
 * later count with the wrong ingredient and run the placement loop past the end of the list.
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

        List<ItemStack> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // Each index pairs with its own ingredient's matching stack even though the inventory
        // holds the items in the opposite order from the recipe.
        assertEquals(2, matched.size());
        assertTrue(matched.get(0).is(Items.IRON_INGOT));
        assertEquals(4, matched.get(0).getCount());
        assertTrue(matched.get(1).is(Items.GUNPOWDER));
        assertEquals(8, matched.get(1).getCount());
    }

    @Test
    void matchIngredientList_partialInventory_keepsEmptyPlaceholderAtUnmatchedIndex() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.GUNPOWDER, 8));

        List<IngredientStack> ingredients = List.of(
                new IngredientStack(Items.IRON_INGOT, 2),
                new IngredientStack(Items.GUNPOWDER, 3));

        List<ItemStack> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        assertEquals(2, matched.size());
        assertTrue(matched.get(0).isEmpty());
        assertTrue(matched.get(1).is(Items.GUNPOWDER));
    }

    @Test
    void matchIngredientList_matchingItemButShortCount_staysEmpty() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(1, new ItemStack(Items.IRON_INGOT, 1));

        List<IngredientStack> ingredients = List.of(new IngredientStack(Items.IRON_INGOT, 2));

        List<ItemStack> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        assertEquals(1, matched.size());
        assertTrue(matched.get(0).isEmpty());
    }
}
