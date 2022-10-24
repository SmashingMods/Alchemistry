package com.smashingmods.alchemistry.api.blockentity.processing;

import com.smashingmods.alchemistry.api.storage.EnergyStorageHandler;

public interface EnergyBlockEntity {

    EnergyStorageHandler initializeEnergyStorage();

    EnergyStorageHandler getEnergyHandler();
}
