package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyBlockEntity {

    IEnergyStorage getEnergy();
}