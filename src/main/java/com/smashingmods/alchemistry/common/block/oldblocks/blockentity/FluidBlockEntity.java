package com.smashingmods.alchemistry.common.block.oldblocks.blockentity;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface FluidBlockEntity {
    LazyOptional<IFluidHandler> getFluidHandler();
}
