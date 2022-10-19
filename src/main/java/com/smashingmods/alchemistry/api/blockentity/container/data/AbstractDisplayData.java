package com.smashingmods.alchemistry.api.blockentity.container.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class AbstractDisplayData implements DisplayData {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public AbstractDisplayData(int pX, int pY, int pWidth, int pHeight) {
        this.x = pX;
        this.y = pY;
        this.width = pWidth;
        this.height = pHeight;
    }

    public Component toTextComponent() {
        String temp = "";
        if (this.toString() != null) {
            temp = this.toString();
        }
        return new TextComponent(temp);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
