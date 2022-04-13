package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyBlockEntity {

    //LazyOptional<IEnergyStorage> getEnergyHolder();

    IEnergyStorage initEnergy();
    IEnergyStorage getEnergy();
}