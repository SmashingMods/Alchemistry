package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins {@link FissionTransferPacket}'s non-creative transfer math: the composition of
 * {@link FissionTransferPacket#buildRecipeIngredients} with the joint matcher, full-match gate and
 * per-slot operation bound from {@link TransferUtils}, exactly as the handler's survival path runs
 * them. The pre-rework handler gated on {@code Inventory#contains}, which scans every compartment
 * (offhand included), but removed via {@code Inventory#findSlotMatchingItem}, which only scans the
 * main inventory -- so an input held only in the offhand passed the gate, the slot lookup returned
 * -1, and {@code getItem(-1)} disconnected the player. The matcher gates and removes over one walk
 * of the main inventory, so absence there is an EMPTY match (no transfer) rather than a crash. The
 * handler itself needs a live server (block entity, ServerPlayer), so these tests drive the
 * extracted statics.
 */
class FissionTransferPacketTest extends BootstrappedTest {

    @Test
    void inputInMainInventory_carriesSlotAndCountForTheOperationBound() {
        FissionRecipe recipe = fissionRecipe(new ItemStack(Items.IRON_INGOT, 2));
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(3, new ItemStack(Items.IRON_INGOT, 7));

        List<IngredientStack> ingredients = FissionTransferPacket.buildRecipeIngredients(recipe);

        // The single input wraps into a one-element list carrying its count -- the operation bound
        // divides the claimed slot by it, so a dropped count would over-transfer for recipes with
        // input counts above 1.
        assertEquals(1, ingredients.size());
        assertTrue(ingredients.get(0).matches(new ItemStack(Items.IRON_INGOT)));
        assertEquals(2, ingredients.get(0).getCount());

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // The match carries the slot the removal debits, so gate and removal agree by construction.
        assertTrue(TransferUtils.isFullMatch(matched));
        assertEquals(3, matched.get(0).slot());
        assertEquals(1, TransferUtils.getMaxOperations(matched, ingredients, false));
        // Max transfer keeps the per-slot bound: 7 / 2 = 3 whole operations.
        assertEquals(3, TransferUtils.getMaxOperations(matched, ingredients, true));
    }

    @Test
    void inputAbsentFromMainInventory_staysEmpty_neverCarriesAForeignSlot() {
        // The offhand shape: an input the old contains() gate found in another compartment is
        // simply absent from the main list the matcher walks, so it must come back EMPTY -- failing
        // the full-match gate -- and can never carry a slot index the removal would crash on.
        FissionRecipe recipe = fissionRecipe(new ItemStack(Items.IRON_INGOT));
        NonNullList<ItemStack> mainInventory = NonNullList.withSize(5, ItemStack.EMPTY);
        mainInventory.set(0, new ItemStack(Items.GUNPOWDER, 8));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(
                mainInventory, FissionTransferPacket.buildRecipeIngredients(recipe));

        assertFalse(TransferUtils.isFullMatch(matched));
        assertTrue(matched.get(0).isEmpty());
        assertEquals(-1, matched.get(0).slot());
    }

    /**
     * A fission recipe over vanilla items -- constructing mod items would need an unfrozen registry
     * (see {@link BootstrappedTest}). Fission inputs are concrete ItemStacks, so unlike the combiner
     * there is no ingredient resolution involved.
     */
    private static FissionRecipe fissionRecipe(ItemStack pInput) {
        return new FissionRecipe(
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "test_fission"),
                "",
                pInput,
                new ItemStack(Items.OAK_SAPLING),
                new ItemStack(Items.STICK));
    }
}
