package com.smashingmods.alchemistry.api.blockentity;

public interface GuiBlockEntity {
    default int getWidth() {
        return 175;
    }
    default int getHeight() {
        return 183;
    }
}
