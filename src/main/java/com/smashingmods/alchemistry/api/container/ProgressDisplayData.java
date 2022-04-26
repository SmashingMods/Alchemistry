package com.smashingmods.alchemistry.api.container;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.ContainerData;

public class ProgressDisplayData extends DisplayData {

    private final ContainerData data;

    public ProgressDisplayData(ContainerData pData, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.data = pData;
    }

    @Override
    public int getValue() {
        return data.get(0);
    }

    @Override
    public int getMaxValue() {
        return data.get(1);
    }

    @Override
    public Component toTextComponent() {
        return new TextComponent("Show Recipes");
    }
}
