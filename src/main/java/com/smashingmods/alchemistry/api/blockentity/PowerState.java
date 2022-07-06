package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.util.StringRepresentable;

public enum PowerState implements StringRepresentable {
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    PowerState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}