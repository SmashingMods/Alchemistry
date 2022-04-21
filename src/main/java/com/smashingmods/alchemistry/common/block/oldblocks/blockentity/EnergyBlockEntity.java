package com.smashingmods.alchemistry.common.block.oldblocks.blockentity;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyBlockEntity {
    LazyOptional<IEnergyStorage> getEnergyHandler();
}