package com.smashingmods.alchemistry.api.container;

import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.inventory.ContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.text.NumberFormat;
import java.util.Locale;

public class FluidDisplayData extends DisplayData {

    private final AbstractAlchemistryBlockEntity blockEntity;
    private final ContainerData containerData;

    public FluidDisplayData(AbstractAlchemistryBlockEntity pBlockEntity, ContainerData pData, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
        this.containerData = pData;
    }

    @Override
    public int getValue() {
        return containerData.get(4);
    }

    @Override
    public int getMaxValue() {
        return containerData.get(5);
    }

    public CustomFluidStorage getFluidHandler() {
        return (CustomFluidStorage) blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseGet(() -> new CustomFluidStorage(0, FluidStack.EMPTY));
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
