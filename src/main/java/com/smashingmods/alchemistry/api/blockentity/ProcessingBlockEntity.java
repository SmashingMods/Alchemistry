package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;

public interface ProcessingBlockEntity {

    void tick();

    void updateRecipe();

    boolean canProcessRecipe();

    void processRecipe();

    <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe);

    <T extends Recipe<Inventory>> Recipe<Inventory> getRecipe();

    int getProgress();

    void setProgress(int pProgress);

    void incrementProgress();

    boolean isRecipeLocked();

    void setRecipeLocked(boolean pRecipeLocked);

    boolean isProcessingPaused();

    void setPaused(boolean pPaused);

    void dropContents();
}
