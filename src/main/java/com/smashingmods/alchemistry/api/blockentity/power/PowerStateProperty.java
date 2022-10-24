package com.smashingmods.alchemistry.api.blockentity.power;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Arrays;
import java.util.Collection;

public class PowerStateProperty extends EnumProperty<PowerState> {
    public static final PowerStateProperty POWER_STATE = PowerStateProperty.create("power_state", PowerState.values());

    protected PowerStateProperty(String pName, Collection<PowerState> pValues) {
        super(pName, PowerState.class, pValues);
    }

    public static PowerStateProperty create(String pName, PowerState... pValues) {
        return create(pName, Arrays.asList(pValues));
    }

    public static PowerStateProperty create(String pName, Collection<PowerState> pValues) {
        return new PowerStateProperty(pName, pValues);
    }
}
