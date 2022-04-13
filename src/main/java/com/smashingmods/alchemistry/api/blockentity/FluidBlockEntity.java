package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

//TODO:: figure out why there is a todo here?
public interface FluidBlockEntity {
    LazyOptional<IFluidHandler> getFluidHandler();
}
