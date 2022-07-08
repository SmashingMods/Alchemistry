package com.smashingmods.alchemistry.api.blockentity;


public interface ProcessingBlockEntity {

    void tick();

    void updateRecipe();

    boolean canProcessRecipe();

    void processRecipe();

//    boolean getPaused();
//
//    void setPaused(boolean pPaused);
}
