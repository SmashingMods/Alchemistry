package com.smashingmods.alchemistry.common.block.oldblocks.container;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class CapabilityDisplayWrapper {

    public int x, y, width, height;

    public CapabilityDisplayWrapper(int pX, int pY, int pWidth, int pHeight) {
        this.x = pX;
        this.y = pY;
        this.width = pWidth;
        this.height = pHeight;
    }

    public abstract int getStored();

    public abstract int getMaxStored();

    public Component toTextComponent() {
        String temp = "";
        if (this.toString() != null) {
            temp = this.toString();
        }
        return new TextComponent(temp);
    }
}