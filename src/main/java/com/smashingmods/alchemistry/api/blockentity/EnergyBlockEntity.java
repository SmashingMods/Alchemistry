package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyBlockEntity {
    CustomEnergyStorage energyHandler = new CustomEnergyStorage(0);
    LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    CustomEnergyStorage initializeEnergyStorage();
}
