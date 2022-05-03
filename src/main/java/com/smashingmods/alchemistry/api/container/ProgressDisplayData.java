package com.smashingmods.alchemistry.api.container;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.ContainerData;

public class ProgressDisplayData extends DisplayData {

    private final ContainerData data;
    private final Direction direction;

    public ProgressDisplayData(ContainerData pData, int pX, int pY, int pWidth, int pHeight, Direction pDirection) {
        super(pX, pY, pWidth, pHeight);
        this.data = pData;
        this.direction = pDirection;
    }

    @Override
    public int getValue() {
        return data.get(0);
    }

    @Override
    public int getMaxValue() {
        return data.get(1);
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public Component toTextComponent() {
        return new TextComponent("Show Recipes");
    }
}
