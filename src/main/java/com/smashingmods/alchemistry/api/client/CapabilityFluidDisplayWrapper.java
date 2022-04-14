package com.smashingmods.alchemistry.api.client;

import com.smashingmods.alchemistry.api.container.BaseContainer;
import com.smashingmods.alchemistry.block.AlchemistryBlockEntity;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityFluidDisplayWrapper extends CapabilityDisplayWrapper {

    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
    private final BaseContainer baseContainer;
    private AlchemistryBlockEntity blockEntity;

    public CapabilityFluidDisplayWrapper(int pX, int pY, int pWidth, int pHeight, BaseContainer pBaseContainer) {
        super(pX, pY, pWidth, pHeight);
        this.baseContainer = pBaseContainer;
    }

    public CapabilityFluidDisplayWrapper(int pX, int pY, int pWidth, int pHeight, BaseContainer pBaseContainer, AlchemistryBlockEntity pBlockEntity) {
        super(pX, pY, pWidth, pHeight);
        this.baseContainer = pBaseContainer;
        this.blockEntity = pBlockEntity;
    }

    public IFluidHandler getHandler() {

        AlchemistryBlockEntity containerBlockEntity = baseContainer.blockEntity;
        LazyOptional<IFluidHandler> containerBlockEntityHandler = containerBlockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        LazyOptional<IFluidHandler> blockEntityHandler = blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

        if (baseContainer != null) {
            return containerBlockEntityHandler.orElseThrow(NullPointerException::new);
        } else {
            return blockEntityHandler.orElseThrow(NullPointerException::new);
        }
    }

    @Override
    public int getStored() {
        return this.getHandler().getFluidInTank(0).getAmount();
        // else return -1;
    }

    @Override
    public int getCapacity() {
        return getHandler().getTankCapacity(0);
    }

    @Override
    public String toString() {
        FluidStack stack = getHandler().getFluidInTank(0);
        String fluidName = "";
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getCapacity());
        if (!stack.isEmpty() && stack.getAmount() > 0) {
            fluidName = I18n.get(stack.getFluid().getAttributes().getTranslationKey());
        }
        return stored + "/" + capacity + " mb " + fluidName;
    }
}