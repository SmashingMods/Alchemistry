package com.smashingmods.alchemistry.api.blockentity.processing;

import com.smashingmods.alchemistry.api.recipe.AbstractProcessingRecipe;

import javax.annotation.Nullable;
import java.util.LinkedList;

public interface ProcessingBlockEntity {

    void tick();

    void updateRecipe();

    boolean canProcessRecipe();

    boolean getCanProcess();

    void setCanProcess(boolean pCanProcess);

    void processRecipe();

    <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe);

    @Nullable
    <R extends AbstractProcessingRecipe> R getRecipe();

    <R extends AbstractProcessingRecipe> LinkedList<R> getAllRecipes();

    int getProgress();

    void setProgress(int pProgress);

    int getMaxProgress();

    void setMaxProgress(int pMaxProgress);

    void incrementProgress();

    boolean isRecipeLocked();

    void setRecipeLocked(boolean pRecipeLocked);

    boolean isProcessingPaused();

    void setPaused(boolean pPaused);

    void setRecipeSelectorOpen(boolean pOpen);

    boolean isRecipeSelectorOpen();

    String getSearchText();

    void setSearchText(String pText);

    void dropContents();
}
