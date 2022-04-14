package com.smashingmods.alchemistry.api.client;

import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.container.BaseContainer;
import com.smashingmods.alchemistry.blocks.AlchemistryBlockEntity;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityEnergyDisplayWrapper extends CapabilityDisplayWrapper {

    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    //public Supplier<IEnergyStorage> energy;
    private BaseContainer container = null;
    private AlchemistryBlockEntity blockEntity = null;

    @SuppressWarnings("unused")
    public CapabilityEnergyDisplayWrapper(int pX, int pY, int pWidth, int pHeight, BaseContainer pContainer) {
        super(pX, pY, pWidth, pHeight);
        this.container = pContainer;
    }

    @SuppressWarnings("unused")
    public CapabilityEnergyDisplayWrapper(int pX, int pY, int pWidth, int pHeight, AlchemistryBlockEntity pBlockEntity) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;

    }

    @Override
    public int getStored() {
        if (container != null) return container.getEnergy();//energy.get().getEnergyStored();
        else if (blockEntity != null) return ((EnergyBlockEntity) blockEntity).getEnergy().getEnergyStored();
        else return -1;
        // else return -1;
    }

    //TODO: figure out why this code is commented
    @SuppressWarnings("CommentedOutCode")
    @Override
    public int getCapacity() {
        if (container != null) return container.getEnergyCapacity();
        else if (blockEntity != null) return ((EnergyBlockEntity) blockEntity).getEnergy().getMaxEnergyStored();
        else return -1;

        //energy.get().getMaxEnergyStored();
        //if (energy.isPresent()) return energy.orElse(null).getMaxEnergyStored();
        //else return -1;//.get().getMaxEnergyStored();
    }

    @Override
    public String toString() {
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getCapacity());
        return stored + "/" + capacity + " FE";
    }
}