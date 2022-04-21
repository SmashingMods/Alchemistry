package com.smashingmods.alchemistry.api.container;

import net.minecraft.network.chat.Component;

public interface IDisplayData {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getStored();

    int getMaxStored();

    Component toTextComponent();
}
