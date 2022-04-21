package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface FluidBlockEntity {
    CustomFluidStorage fluidHandler = new CustomFluidStorage(0, FluidStack.EMPTY);
    LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidHandler);

    CustomFluidStorage initializeFluidStorage();
}
