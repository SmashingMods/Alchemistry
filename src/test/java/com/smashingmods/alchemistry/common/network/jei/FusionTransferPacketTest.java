package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
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
 * Pins {@link FusionTransferPacket}'s non-creative transfer math: the composition of
 * {@link FusionTransferPacket#buildRecipeIngredients} with the joint matcher, full-match gate and
 * per-slot operation bound from {@link TransferUtils}, exactly as the handler's survival path runs
 * them. Most shipped fusion recipes fuse an element with itself (hydrogen + hydrogen -&gt; helium),
 * and the pre-rework handler looked each input's slot up independently: both lookups resolved the
 * same stack, the operation count was taken per input, and the second removal came back empty while
 * the placement still inserted both shares -- duplicating the difference. It also gated on
 * {@code Inventory#contains}, which scans the offhand the main-only slot lookup never returns, so an
 * input held only there crashed the removal on slot -1. The handler itself needs a live server
 * (block entity, ServerPlayer), so these tests drive the extracted statics; the in-world debit of
 * two distinct inputs is covered by the {@code fusionTransferDebitsBothInputs} gametest.
 */
class FusionTransferPacketTest extends BootstrappedTest {

    @Test
    void buildRecipeIngredients_carriesEachInputsItemAndCount() {
        FusionRecipe recipe = fusionRecipe(new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.GUNPOWDER, 3));

        List<IngredientStack> ingredients = FusionTransferPacket.buildRecipeIngredients(recipe);

        // The operation bound divides each claimed slot by these counts, so dropping a count on the
        // wrap (the IngredientStack(Item) constructors default to 1) would break the joint math for
        // any recipe with input counts above 1.
        assertEquals(2, ingredients.size());
        assertTrue(ingredients.get(0).matches(new ItemStack(Items.IRON_INGOT)));
        assertEquals(2, ingredients.get(0).getCount());
        assertTrue(ingredients.get(1).matches(new ItemStack(Items.GUNPOWDER)));
        assertEquals(3, ingredients.get(1).getCount());
    }

    @Test
    void sameElementRecipe_oneStack_jointClaimsBoundOperationsToTheSharedStack() {
        // The hydrogen + hydrogen -> helium shape: both inputs the same item at count 1, the player
        // holding one stack of 4.
        FusionRecipe recipe = fusionRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT));
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(2, new ItemStack(Items.IRON_INGOT, 4));

        List<IngredientStack> ingredients = FusionTransferPacket.buildRecipeIngredients(recipe);
        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        // Both inputs claim the one stack jointly, carrying the same slot.
        assertTrue(TransferUtils.isFullMatch(matched));
        assertEquals(2, matched.get(0).slot());
        assertEquals(2, matched.get(1).slot());

        // Max transfer divides the stack by the joint per-operation claim (1 + 1): 4 / 2 = 2
        // operations -- not the per-input 4 the old independent lookups authorized.
        int maxOperations = TransferUtils.getMaxOperations(matched, ingredients, true);
        assertEquals(2, maxOperations);

        // No-dupe invariant: the handler debits inputCount x operations per claim and places the
        // same amounts into the machine, so the total debit on the shared slot must not exceed what
        // the stack holds -- then every removeItem succeeds in full and placement mirrors exactly
        // what left the inventory. The old count (4 operations) debited 4 + 4 against a stack of 4:
        // the second removal came back empty while the machine still received both shares.
        int totalDebit = recipe.getInput1().getCount() * maxOperations + recipe.getInput2().getCount() * maxOperations;
        assertEquals(inventory.get(2).getCount(), totalDebit);
    }

    @Test
    void sameElementRecipe_splitStacks_maxTransferDrawsBothClaimsFromTheLargestStack() {
        // The field failure: 64 hydrogen in one slot and a 3-stack at a LOWER slot index. Claiming
        // by first match steered both inputs into the 3-stack, so the per-slot bound collapsed to
        // 3 / (1 + 1) = 1 operation and a shift-click moved exactly 2 items out of a 67-item
        // inventory. Both claims must land on the 64-stack instead: 64 / 2 = 32 operations.
        FusionRecipe recipe = fusionRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT));
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(1, new ItemStack(Items.IRON_INGOT, 3));
        inventory.set(3, new ItemStack(Items.IRON_INGOT, 64));

        List<IngredientStack> ingredients = FusionTransferPacket.buildRecipeIngredients(recipe);
        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        assertTrue(TransferUtils.isFullMatch(matched));
        assertEquals(3, matched.get(0).slot());
        assertEquals(3, matched.get(1).slot());

        int maxOperations = TransferUtils.getMaxOperations(matched, ingredients, true);
        assertEquals(32, maxOperations);

        // Quantity-exact, not just conserving: the moved total is the full 64-stack (1 x 32 per
        // input), funded entirely by the claimed slot, with the 3-stack left untouched.
        int totalDebit = recipe.getInput1().getCount() * maxOperations + recipe.getInput2().getCount() * maxOperations;
        assertEquals(64, totalDebit);
        assertEquals(inventory.get(3).getCount(), totalDebit);
    }

    @Test
    void inputAbsentFromMainInventory_staysEmpty_neverCarriesAForeignSlot() {
        // The offhand shape: Inventory#contains scans every compartment, so an element held only in
        // the offhand passed the old gate while the main-only slot lookup returned -1 and
        // getItem(-1) disconnected the player. The matcher only ever walks the main list it is
        // handed, so an input absent from it must come back EMPTY -- failing the full-match gate --
        // and can never carry a slot index the removal would crash on.
        FusionRecipe recipe = fusionRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT));
        NonNullList<ItemStack> mainInventory = NonNullList.withSize(5, ItemStack.EMPTY);
        mainInventory.set(0, new ItemStack(Items.GUNPOWDER, 8));

        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(
                mainInventory, FusionTransferPacket.buildRecipeIngredients(recipe));

        assertFalse(TransferUtils.isFullMatch(matched));
        for (TransferUtils.SlotMatch match : matched) {
            assertTrue(match.isEmpty());
            assertEquals(-1, match.slot());
        }
    }

    @Test
    void distinctElementRecipe_separateSlots_stillTransfers() {
        // The joint rework must not regress the ordinary case: two different elements in two
        // different slots still fully match, each by its own slot.
        FusionRecipe recipe = fusionRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GUNPOWDER));
        NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        inventory.set(1, new ItemStack(Items.IRON_INGOT, 4));
        inventory.set(3, new ItemStack(Items.GUNPOWDER, 6));

        List<IngredientStack> ingredients = FusionTransferPacket.buildRecipeIngredients(recipe);
        List<TransferUtils.SlotMatch> matched = TransferUtils.matchIngredientListToItemStack(inventory, ingredients);

        assertTrue(TransferUtils.isFullMatch(matched));
        assertEquals(1, matched.get(0).slot());
        assertEquals(3, matched.get(1).slot());
        // A single transfer stays one operation; max transfer keeps the per-slot bound min(4/1, 6/1).
        assertEquals(1, TransferUtils.getMaxOperations(matched, ingredients, false));
        assertEquals(4, TransferUtils.getMaxOperations(matched, ingredients, true));
    }

    /**
     * A fusion recipe over vanilla items -- constructing mod items would need an unfrozen registry
     * (see {@link BootstrappedTest}). Fusion inputs are concrete ItemStacks, so unlike the combiner
     * there is no ingredient resolution involved.
     */
    private static FusionRecipe fusionRecipe(ItemStack pInput1, ItemStack pInput2) {
        return new FusionRecipe(
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "test_fusion"),
                "",
                pInput1,
                pInput2,
                new ItemStack(Items.OAK_SAPLING));
    }
}
