package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum PowerState implements StringRepresentable {
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    PowerState(String name) {
        this.name = name;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }
}