package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TransferUtils {

    /**
     * One ingredient's claim on the player inventory: the live matched stack and the slot
     * index holding it. {@link #EMPTY} marks an ingredient the inventory cannot satisfy.
     * Carrying the slot lets the caller remove exactly the stack it matched; re-looking the
     * stack up at removal time (e.g. {@code Inventory#findSlotMatchingItem}) returns -1 once
     * an earlier removal empties a shared stack.
     */
    public record SlotMatch(ItemStack itemStack, int slot) {

        public static final SlotMatch EMPTY = new SlotMatch(ItemStack.EMPTY, -1);

        public boolean isEmpty() {
            return itemStack.isEmpty();
        }
    }

    public static int getMaxOperations(ItemStack pRecipeInput, boolean pMaxTransfer) {
        return getMaxOperations(pRecipeInput, ItemStack.EMPTY, pMaxTransfer, true);
    }

    public static int getMaxOperations(List<ItemStack> pRecipeInputList, boolean pMaxTransfer) {
        return getMaxOperations(pRecipeInputList, new ArrayList<>(), pMaxTransfer, true);
    }

    public static int getMaxOperations(ItemStack pRecipeInput, ItemStack pInventoryInput, boolean pMaxTransfer, boolean pCreative) {
        int maxCount;
        if (pMaxTransfer) {
            if (pCreative) {
                maxCount = pRecipeInput.getMaxStackSize();
            } else {
                maxCount = pInventoryInput.getCount();
            }
        } else {
            maxCount = pRecipeInput.getCount();
        }
        return maxCount / pRecipeInput.getCount();
    }

    public static int getMaxOperations(List<ItemStack> pRecipeInputList, @Nonnull List<ItemStack> pInventoryItems, boolean pMaxTransfer, boolean pCreative) {

        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < pRecipeInputList.size(); i++) {
            if (pRecipeInputList.get(i).getCount() < 1) {
                ints.add(0);
                break;
            }
            int maxCount;
            if (pMaxTransfer) {
                if (pCreative) {
                    maxCount = pRecipeInputList.get(i).getMaxStackSize();
                } else {
                    maxCount = pInventoryItems.get(i).getCount();
                }
            } else {
                maxCount = pRecipeInputList.get(i).getCount();
            }
            ints.add(maxCount / pRecipeInputList.get(i).getCount());
        }
        return Collections.min(ints);
    }

    /**
     * Operation count for a joint match (see {@link #matchIngredientListToItemStack}). Without
     * max transfer this is a single operation. With it, each slot bounds the count by its stack
     * size divided by the TOTAL per-operation claim on that slot, so two ingredients drawing on
     * the same stack cannot authorize more removals than the stack holds; for an ingredient with
     * a slot of its own this is the plain stackCount / recipeCount bound.
     */
    public static int getMaxOperations(List<SlotMatch> pMatchedInput, List<IngredientStack> pRecipeInputList, boolean pMaxTransfer) {
        if (pRecipeInputList.isEmpty()) {
            return 0;
        }
        Map<Integer, Integer> claimedPerSlot = new HashMap<>();
        for (int i = 0; i < pRecipeInputList.size(); i++) {
            if (pRecipeInputList.get(i).getCount() < 1) {
                return 0;
            }
            claimedPerSlot.merge(pMatchedInput.get(i).slot(), pRecipeInputList.get(i).getCount(), Integer::sum);
        }
        if (!pMaxTransfer) {
            return 1;
        }
        List<Integer> ints = new ArrayList<>();
        for (SlotMatch slotMatch : pMatchedInput) {
            ints.add(slotMatch.itemStack().getCount() / claimedPerSlot.get(slotMatch.slot()));
        }
        return Collections.min(ints);
    }

    public static ItemStack matchIngredientToItemStack(NonNullList<ItemStack> pItems, IngredientStack pIngredientStack) {
        AtomicReference<ItemStack> atomicItem = new AtomicReference<>();
        boolean test = pItems.stream().anyMatch(itemStack -> {
            boolean matches = pIngredientStack.matches(itemStack);
            if (matches && itemStack.getCount() >= pIngredientStack.getCount()) {
                atomicItem.set(itemStack);
                return true;
            }
            return false;
        });
        return test ? atomicItem.get() : ItemStack.EMPTY;
    }

    /**
     * Pairs each ingredient with a matching inventory stack and its slot, jointly: a slot's
     * count is consumed as ingredients claim it, so two ingredients satisfiable by the same
     * stack only both match while the stack still holds enough for both, and an ingredient the
     * consumed slot can no longer cover moves on to the next matching slot. The returned list
     * is always index-parallel to {@code pIngredientStackList}: an ingredient the inventory
     * cannot satisfy is represented by {@link SlotMatch#EMPTY} rather than skipped, so
     * positions keep lining up with the recipe input.
     */
    public static List<SlotMatch> matchIngredientListToItemStack(NonNullList<ItemStack> pItems, List<IngredientStack> pIngredientStackList) {
        int[] remaining = new int[pItems.size()];
        for (int slot = 0; slot < pItems.size(); slot++) {
            remaining[slot] = pItems.get(slot).getCount();
        }
        List<SlotMatch> toReturn = new ArrayList<>();
        for (IngredientStack ingredientStack : pIngredientStackList) {
            SlotMatch match = SlotMatch.EMPTY;
            for (int slot = 0; slot < pItems.size(); slot++) {
                if (ingredientStack.matches(pItems.get(slot)) && remaining[slot] >= ingredientStack.getCount()) {
                    remaining[slot] -= ingredientStack.getCount();
                    match = new SlotMatch(pItems.get(slot), slot);
                    break;
                }
            }
            toReturn.add(match);
        }
        return toReturn;
    }

    /**
     * Whether the matched inventory slots cover every recipe input. {@code pInventoryInput} is
     * index-parallel to the recipe input with EMPTY at every unmatched position (see
     * {@link #matchIngredientListToItemStack}); any EMPTY means the player lacks an ingredient --
     * or, the match being joint, that a shared stack cannot cover another claim -- and a partial
     * transfer must not run: counts would pair with the wrong ingredients and the removal loop
     * could run a stack dry part-way through. An empty list (a degenerate zero-input recipe) is
     * not transferable either. The non-creative transfer paths gate on this before removing
     * anything.
     */
    public static boolean isFullMatch(List<SlotMatch> pInventoryInput) {
        return !pInventoryInput.isEmpty() && pInventoryInput.stream().noneMatch(SlotMatch::isEmpty);
    }
}
