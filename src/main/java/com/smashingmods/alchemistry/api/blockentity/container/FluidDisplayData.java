package com.smashingmods.alchemistry.api.blockentity.container;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemistry.api.storage.FluidStorageHandler;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.text.NumberFormat;
import java.util.Locale;

public class FluidDisplayData extends AbstractDisplayData {

    private final AbstractFluidBlockEntity blockEntity;

    public FluidDisplayData(AbstractFluidBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
    }

    @Override
    public int getValue() {
        return blockEntity.getFluidStorage().getFluidAmount();
    }

    @Override
    public int getMaxValue() {
        return blockEntity.getFluidStorage().getCapacity();
    }

    public FluidStorageHandler getFluidHandler() {
        return (FluidStorageHandler) blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseGet(() -> new FluidStorageHandler(0, FluidStack.EMPTY));
    }

    @Override
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        FluidStack fluidStack = getFluidHandler().getFluidStack();

        boolean emptyFluid = fluidStack.isFluidEqual(FluidStack.EMPTY);

        String fluidName = emptyFluid ? "" : String.format(" %s", I18n.get(fluidStack.getTranslationKey()).toLowerCase());
        String stored = numberFormat.format(getValue());
        String capacity = numberFormat.format(getMaxValue());
        return String.format("%s/%s mb%s", stored, capacity, fluidName);
    }
}
