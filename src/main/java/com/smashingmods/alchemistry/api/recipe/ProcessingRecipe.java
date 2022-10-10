package com.smashingmods.alchemistry.api.recipe;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;

public interface ProcessingRecipe extends Recipe<Inventory> {

    ProcessingRecipe copy();

    Object getInput();

    Object getOutput();
}
