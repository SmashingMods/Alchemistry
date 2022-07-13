package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;

public interface EnergyBlockEntity {

    CustomEnergyStorage initializeEnergyStorage();

    CustomEnergyStorage getEnergyHandler();
}
