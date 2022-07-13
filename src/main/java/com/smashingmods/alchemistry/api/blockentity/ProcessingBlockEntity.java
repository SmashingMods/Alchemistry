package com.smashingmods.alchemistry.api.blockentity;


public interface ProcessingBlockEntity {

    void tick();

    void updateRecipe();

    boolean canProcessRecipe();

    void processRecipe();

    int getProgress();

    void setProgress(int pProgress);

    void incrementProgress();

    boolean getRecipeLocked();

    void setRecipeLocked(boolean pRecipeLocked);

    boolean getPaused();

    void setPaused(boolean pPaused);

    void dropContents();
}
