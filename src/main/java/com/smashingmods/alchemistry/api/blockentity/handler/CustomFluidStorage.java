package com.smashingmods.alchemistry.api.blockentity.handler;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CustomFluidStorage extends FluidTank {

    public CustomFluidStorage(int pCapacity, FluidStack pFluidStack) {
        super(pCapacity);
        this.fill(pFluidStack, FluidAction.EXECUTE);
    }

    public void setAmount(int pValue) {
        this.fluid.setAmount(pValue);
    }

    public void setFluid(FluidStack pFluidStack) {
        this.drain(this.capacity, FluidAction.EXECUTE);
        this.fill(pFluidStack, FluidAction.EXECUTE);
    }

    public FluidStack getFluidStack() {
        return this.fluid;
    }
}
