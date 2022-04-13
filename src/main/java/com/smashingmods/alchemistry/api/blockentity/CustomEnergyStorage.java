package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    @SuppressWarnings("unused")
    public CustomEnergyStorage(int pCapacity) {
        super(pCapacity);
    }

    @SuppressWarnings("unused")
    public CustomEnergyStorage(int pCapacity, int pMaxTransfer) {
        super(pCapacity, pMaxTransfer);
    }

    @SuppressWarnings("unused")
    public CustomEnergyStorage(int pCapacity, int pMaxReceive, int pMaxExtract) {
        super(pCapacity, pMaxReceive, pMaxExtract);
    }

    @SuppressWarnings("unused")
    public CustomEnergyStorage(int pReceive, int pMaxReceive, int pMaxExtract, int pEnergy) {
        super(pReceive, pMaxReceive, pMaxExtract, pEnergy);
    }

    @SuppressWarnings("unused")
    public int extractEnergyInternal(int pMaxExtract, boolean pSimulate) {
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, pMaxExtract));
        if (!pSimulate) {
            energy -= energyExtracted;
            onEnergyChanged();
        }
        return energyExtracted;
    }

    //TODO:: figure out why this has a return value
    @SuppressWarnings("UnusedReturnValue")
    public int receiveEnergyInternal(int pMaxReceive, boolean pSimulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, pMaxReceive));
        if (!pSimulate) {
            energy += energyReceived;
            onEnergyChanged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0) onEnergyChanged();
        return extracted;
    }

    @Override
    public int receiveEnergy(int pMaxReceive, boolean pSimulate) {
        int received = super.receiveEnergy(pMaxReceive, pSimulate);
        if (received > 0) onEnergyChanged();
        return received;
    }

    @SuppressWarnings("unused")
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void onEnergyChanged() {
    }
}