package com.smashingmods.alchemistry.api.container;

import net.minecraft.world.inventory.ContainerData;

public class ProgressDisplayData extends DisplayData {

    private final ContainerData data;
    private final Direction2D direction2D;
    private final int valueSlot;
    private final int maxValueSlot;

    public ProgressDisplayData(ContainerData pData, int pValueSlot, int pMaxValueSlot, int pX, int pY, int pWidth, int pHeight, Direction2D pDirection2D) {
        super(pX, pY, pWidth, pHeight);
        this.data = pData;
        this.direction2D = pDirection2D;
        this.valueSlot = pValueSlot;
        this.maxValueSlot = pMaxValueSlot;
    }

    @Override
    public int getValue() {
        return data.get(valueSlot);
    }

    @Override
    public int getMaxValue() {
        return data.get(maxValueSlot);
    }

    public Direction2D getDirection() {
        return direction2D;
    }
}
