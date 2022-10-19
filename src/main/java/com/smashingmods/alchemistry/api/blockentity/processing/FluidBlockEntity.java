package com.smashingmods.alchemistry.api.blockentity.processing;

import com.smashingmods.alchemistry.api.storage.FluidStorageHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;

public interface FluidBlockEntity {

    FluidStorageHandler initializeFluidStorage();

    FluidStorageHandler getFluidStorage();

    ProcessingSlotHandler initializeSlotHandler();

    ProcessingSlotHandler getSlotHandler();
}
