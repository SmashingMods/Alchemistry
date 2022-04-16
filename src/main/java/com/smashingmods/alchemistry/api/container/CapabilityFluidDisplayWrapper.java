package com.smashingmods.alchemistry.api.container;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityFluidDisplayWrapper extends CapabilityDisplayWrapper {

    private final BaseContainer container;

    public CapabilityFluidDisplayWrapper(int pX, int pY, int pWidth, int pHeight, BaseContainer pContainer) {
        super(pX, pY, pWidth, pHeight);
        this.container = pContainer;
    }

    public IFluidHandler getHandler() {
        LazyOptional<IFluidHandler> handler = container.blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        return handler.orElseThrow(NullPointerException::new);
    }

    @Override
    public int getStored() {
        return getHandler().getFluidInTank(0).getAmount();
    }

    public int getMaxStored() {
        return getHandler().getTankCapacity(0);
    }

    @Override
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String fluidName = "";
        String stored = numberFormat.format(getStored());
        String capacity = numberFormat.format(getMaxStored());

        FluidStack stack = getHandler().getFluidInTank(0);
        if (!stack.isEmpty() && stack.getAmount() > 0) {
            fluidName = I18n.get(stack.getFluid().getAttributes().getTranslationKey());
        }
        return stored + "/" + capacity + " mb " + fluidName;
    }
}