package com.smashingmods.alchemistry.api.container;

import net.minecraft.world.inventory.ContainerData;

import java.text.NumberFormat;
import java.util.Locale;

public class EnergyDisplayData extends DisplayData {

    private final ContainerData data;

    public EnergyDisplayData(ContainerData pData, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.data = pData;
    }

    @Override
    public int getValue() {
        return data.get(2);
    }

    @Override
    public int getMaxValue() {
        return data.get(3);
    }

    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
        String stored = numFormat.format(getValue());
        String capacity = numFormat.format(getMaxValue());
        return stored + "/" + capacity + " FE";
    }
}
