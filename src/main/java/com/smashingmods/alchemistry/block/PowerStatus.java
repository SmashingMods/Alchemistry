package com.smashingmods.alchemistry.block;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum PowerStatus implements StringRepresentable {
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    PowerStatus(String name) {
        this.name = name;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }
}