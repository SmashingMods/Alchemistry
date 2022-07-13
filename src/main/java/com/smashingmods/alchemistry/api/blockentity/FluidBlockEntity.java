package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;

public interface FluidBlockEntity {

    CustomFluidStorage initializeFluidStorage();

    CustomFluidStorage getFluidStorage();

    CustomItemStackHandler initializeItemHandler();

    CustomItemStackHandler getItemHandler();
}
