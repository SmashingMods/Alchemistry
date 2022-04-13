package com.smashingmods.alchemistry.api.blockentity;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("unused")
public class CustomFluidStorage extends FluidTank {

    public CustomFluidStorage(int pCapacity, FluidStack pFluidStack) {
        super(pCapacity);
        this.fill(pFluidStack, FluidAction.EXECUTE);
    }

    public void set(FluidStack pFluidStack) {
        this.drain(this.capacity, FluidAction.EXECUTE);
        this.fill(pFluidStack, FluidAction.EXECUTE);
    }
}
