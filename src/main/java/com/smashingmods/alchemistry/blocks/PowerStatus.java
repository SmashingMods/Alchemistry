package com.smashingmods.alchemistry.blocks;

import net.minecraft.util.StringRepresentable;

public enum PowerStatus implements StringRepresentable {
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    PowerStatus(String name) {
        this.name = name;
    }

    
    @Override
    public String getSerializedName() {
        return this.name;
    }
}