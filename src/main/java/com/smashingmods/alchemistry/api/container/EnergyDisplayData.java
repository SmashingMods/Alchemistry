package com.smashingmods.alchemistry.api.container;

import net.minecraft.world.inventory.ContainerData;

import java.text.NumberFormat;
import java.util.Locale;

public class EnergyDisplayData extends DisplayData {

    private final ContainerData data;
    private final int valueSlot;
    private final int maxValueSlot;

    public EnergyDisplayData(ContainerData pData, int pValueSlot, int pMaxValueSlot, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.data = pData;
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

    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
        String stored = numFormat.format(getValue());
        String capacity = numFormat.format(getMaxValue());
        return stored + "/" + capacity + " FE";
    }
}
