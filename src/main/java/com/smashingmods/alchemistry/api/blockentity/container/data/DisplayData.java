package com.smashingmods.alchemistry.api.blockentity.container.data;

import net.minecraft.network.chat.Component;

public interface DisplayData {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getValue();

    int getMaxValue();

    Component toTextComponent();
}
