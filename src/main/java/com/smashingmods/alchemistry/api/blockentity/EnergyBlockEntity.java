package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyBlockEntity {
    LazyOptional<IEnergyStorage> getEnergyHandler();
}