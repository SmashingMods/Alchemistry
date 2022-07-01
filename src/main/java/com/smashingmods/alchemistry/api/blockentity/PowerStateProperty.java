package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Collection;

public class PowerStateProperty extends EnumProperty<PowerState> {

    public static final EnumProperty<PowerState> STATUS = PowerStateProperty.create("status", PowerState.class, PowerState.values());

    protected PowerStateProperty(String pName, Collection<PowerState> pValues) {
        super(pName, PowerState.class, pValues);
    }

    public static PowerStateProperty create(String pName, Collection<PowerState> pValues) {
        return new PowerStateProperty(pName, pValues);
    }
}
