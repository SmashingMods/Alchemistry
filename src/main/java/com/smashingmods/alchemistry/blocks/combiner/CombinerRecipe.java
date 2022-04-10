package com.smashingmods.alchemistry.blocks.combiner;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemistry.misc.ProcessingRecipe;
import com.smashingmods.alchemylib.tiles.CustomStackHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CombinerRecipe extends ProcessingRecipe {
    public final ItemStack output;
    public final List<ItemStack> inputs = new ArrayList<>();
    public final Set<Integer> nonEmptyIndices = new HashSet<>();

    //String gamestage;

    public CombinerRecipe(ResourceLocation id, String group, List<ItemStack> input, ItemStack output) {
        super(Registration.COMBINER_TYPE, id, group, Ingredient.EMPTY, ItemStack.EMPTY);
        this.output = output;
        for (int i = 0; i < 9; i++) {
            Object temp;
            if (input.size() > i) temp = input.get(i);
            else temp = null;
            if (temp instanceof ItemStack) {
                inputs.add(((ItemStack) temp).copy());
            } else if (temp instanceof Item) {
                inputs.add(new ItemStack((Item) temp));
            } else if (temp instanceof Block) {
                inputs.add(new ItemStack((Block) temp));
            } else {
                inputs.add(ItemStack.EMPTY);
            }
            if (!inputs.get(i).isEmpty()) nonEmptyIndices.add(i);
        }
        assert inputs.size() == 9;
    }

    @Override
    public String toString() {
        return "input=" + inputs + "\toutput=" + output;
    }

    public List<ItemStack> getEmptyStrippedInputs() {
        return inputs.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    public boolean matchesHandlerStacks(CustomStackHandler handler) {
        int matchingStacks = 0;

        for (int index = 0; index < this.inputs.size(); index++) {
            ItemStack recipeStack = this.inputs.get(index);
            ItemStack handlerStack = handler.getStackInSlot(index);
            if ((handlerStack.getItem() == Registration.SLOT_FILLER_ITEM.get() || handlerStack.isEmpty()) && recipeStack.isEmpty()) {
                matchingStacks++;
            } else if (handlerStack.isEmpty() || recipeStack.isEmpty()) continue;
            else if (ItemStack.isSameItemSameTags(handlerStack, recipeStack)
                    && handlerStack.getCount() >= recipeStack.getCount()) {
                //&& (handlerStack.itemDamage == recipeStack.itemDamage || recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                matchingStacks++;
            }
        }
        return matchingStacks == 9;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.COMBINER_SERIALIZER.get();
    }
}
