package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface FluidBlockEntity {
    LazyOptional<IFluidHandler> getFluidHandler();
}
