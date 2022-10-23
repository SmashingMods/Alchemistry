package com.smashingmods.alchemistry.api.recipe.compatability;

import net.minecraft.util.StringRepresentable;

public enum ThermalMetalType implements StringRepresentable {
    BRONZE("bronze"),
    INVAR("invar"),
    ELECTRUM("electrum"),
    CONSTANTAN("constantan"),
    SIGNALUM("signalum"),
    LUMIUM("lumium"),
    ENDERIUM("enderium");

    private final String type;

    ThermalMetalType(String pType) {
        this.type = pType;
    }

    @Override
    public String getSerializedName() {
        return type;
    }
}