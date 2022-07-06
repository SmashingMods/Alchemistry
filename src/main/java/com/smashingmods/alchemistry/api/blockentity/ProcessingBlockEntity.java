package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.world.level.Level;

public interface ProcessingBlockEntity {

    void tick(Level pLevel);

    void updateRecipe(Level pLevel);

    boolean canProcessRecipe();

    void processRecipe();

//    boolean getPaused();
//
//    void setPaused(boolean pPaused);
}
