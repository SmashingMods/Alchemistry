package com.smashingmods.alchemistry.common.block.oldblocks.blockentity;

public interface GuiBlockEntity {
    default int getWidth() {
        return 175;
    }
    default int getHeight() {
        return 183;
    }
}
