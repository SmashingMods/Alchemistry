package com.smashingmods.alchemistry.api.client;

import com.smashingmods.alchemistry.api.blockentity.BaseBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.container.BaseContainer;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityEnergyDisplayWrapper extends CapabilityDisplayWrapper {

    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    //public Supplier<IEnergyStorage> energy;
    private BaseContainer container = null;
    private BaseBlockEntity tile = null;

    @SuppressWarnings("unused")
    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height, BaseContainer container) {
        super(x, y, width, height);
        this.container = container;
    }

    @SuppressWarnings("unused")
    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height, BaseBlockEntity tile) {
        super(x, y, width, height);
        this.tile = tile;

    }

    @Override
    public int getStored() {
        if (container != null) return container.getEnergy();//energy.get().getEnergyStored();
        else if (tile != null) return ((EnergyBlockEntity) tile).getEnergy().getEnergyStored();
        else return -1;
        // else return -1;
    }

    //TODO: figure out why this code is commented
    @SuppressWarnings("CommentedOutCode")
    @Override
    public int getCapacity() {
        if (container != null) return container.getEnergyCapacity();
        else if (tile != null) return ((EnergyBlockEntity) tile).getEnergy().getMaxEnergyStored();
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