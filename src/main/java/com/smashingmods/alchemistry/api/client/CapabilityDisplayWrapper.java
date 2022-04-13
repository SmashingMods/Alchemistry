package com.smashingmods.alchemistry.api.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class CapabilityDisplayWrapper {

    public int x, y, width, height;

    public CapabilityDisplayWrapper(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract int getStored();

    public abstract int getCapacity();

    public Component toTextComponent() {
        String temp = "";
        if (this.toString() != null) temp = this.toString();
        return new TextComponent(temp);
    }
}