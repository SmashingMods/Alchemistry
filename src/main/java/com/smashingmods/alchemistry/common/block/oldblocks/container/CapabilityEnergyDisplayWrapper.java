package com.smashingmods.alchemistry.common.block.oldblocks.container;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityEnergyDisplayWrapper extends CapabilityDisplayWrapper {

    private final BaseContainer container;

    public CapabilityEnergyDisplayWrapper(int pX, int pY, int pWidth, int pHeight, BaseContainer pContainer) {
        super(pX, pY, pWidth, pHeight);
        this.container = pContainer;
    }

    public int getStored() {
        return container.getEnergy();
//        return container.blockEntity.getCapability(CapabilityEnergy.ENERGY)
//                .map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getMaxStored() {
        return container.getEnergyCapacity();
//        return container.blockEntity.getCapability(CapabilityEnergy.ENERGY)
//                .map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getMaxStored());
        return stored + "/" + capacity + " FE";
    }
}