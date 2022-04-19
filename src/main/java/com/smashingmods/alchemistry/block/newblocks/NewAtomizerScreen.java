package com.smashingmods.alchemistry.block.newblocks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewAtomizerScreen extends AbstractContainerScreen<NewAtomizerMenu> {

    protected final List<DisplayData<?>> displayData = new ArrayList<>();

    public NewAtomizerScreen(NewAtomizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 8, 21, 16, 46));
        displayData.add(new FluidDisplayData(pMenu.getBlockEntity(), 44, 21, 16, 46));
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        ResourceLocation bgTexture = new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png");
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, bgTexture);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component displayname = new TranslatableComponent("alchemistry.container.atomizer");
        drawString(pPoseStack, this.font, displayname, this.imageWidth / 2 - this.font.width(displayname) / 2, -10, Color.WHITE.getRGB());
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pDelta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pDelta);
        renderTooltip(pPoseStack, pMouseX, pMouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        displayData.forEach(data -> {
            if (data instanceof EnergyDisplayData) {
                ResourceLocation energyBarTexture = new ResourceLocation(Alchemistry.MODID, "textures/gui/template.png");
                this.drawEnergyBar(pPoseStack, (EnergyDisplayData) data, energyBarTexture, 0, 0);
            }
            if (data instanceof FluidDisplayData) {
                this.drawFluidTank((FluidDisplayData) data, x + data.getX(), y + data.getY());
            }
        });

        displayData.stream().filter(data ->
                pMouseX >= data.getX() + x &&
                pMouseX <= data.getX() + x + data.getWidth() &&
                pMouseY >= data.getY() + y &&
                pMouseY <= data.getY() + y + data.getHeight()
                ).forEach(displayWrapper -> renderTooltip(pPoseStack, displayWrapper.toTextComponent(), pMouseX, pMouseY));
    }

    public int getEnergyBarScaled(int pPixels, int pCount, int pMax) {
        if (pCount > 0 && pMax > 0) {
            return pCount * pPixels / pMax;
        } else {
            return 0;
        }
    }

    public void drawEnergyBar(PoseStack pPoseStack, EnergyDisplayData pData, ResourceLocation pResourceLocation, int pTextureX, int pTextureY) {
        if (pData.getStored() > 0) {
            int x = pData.getX() + (this.width - this.imageWidth) / 2;
            int y = pData.getY() + (this.height - this.imageHeight) / 2;
            int vHeight = this.getEnergyBarScaled(pData.getHeight(), pData.getStored(), pData.getMaxStored());
            RenderSystem.setShaderTexture(0, pResourceLocation);
            this.blit(pPoseStack, x, y + pData.getHeight() - vHeight, pTextureX, pTextureY, pData.getWidth(), vHeight);
        }
    }

    public void drawFluidTank(FluidDisplayData pData, int pTextureX, int pTextureY) {
        if (pData.getStored() > 5) {
            Minecraft.getInstance().textureManager.bindForSetup(InventoryMenu.BLOCK_ATLAS);

            FluidStack fluidStack = pData.getHandler().map(handler ->
                    new FluidStack(handler.getFluidInTank(0).getFluid(), pData.getStored()))
                    .orElse(new FluidStack(Fluids.LAVA, 0));

            if (fluidStack.getFluid() == null || fluidStack.getAmount() <= 0) return;

            TextureAtlasSprite icon = getStillTexture(fluidStack.getFluid());
            float minU = icon.getU0();
            float maxU = icon.getU1();
            float minV = icon.getV0();
            float maxV = icon.getV1();

            int renderAmount = Math.max(Math.min(pData.getHeight(), pData.getStored() * pData.getHeight() / pData.getMaxStored()), 1);
            int posY = pTextureY + pData.getHeight() - renderAmount;

            setFluidColor(fluidStack.getFluid().getAttributes().getColor());
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            for (int width = 0; width < pData.getWidth(); width++) {
                for (int height = 0; height < pData.getHeight(); height++) {
                    int drawWidth = Math.min(pData.getWidth() - width, 16);
                    int drawHeight = Math.min(renderAmount - height, 16);
                    int drawX = pTextureX + width;
                    int drawY = posY + height;

                    float pV = minV + (maxV - minV) * drawHeight / 16f;
                    float pU = minU + (maxU - minU) * drawWidth / 16f;

                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferBuilder = tesselator.getBuilder();

                    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    bufferBuilder.vertex(drawX, drawY + drawHeight, 0).uv(minU, pV).endVertex();
                    bufferBuilder.vertex((drawX + drawWidth), (drawY + drawHeight), 0).uv(pU, pV).endVertex();
                    bufferBuilder.vertex((drawX + drawWidth), drawY, 0).uv(pU, minV).endVertex();
                    bufferBuilder.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
                    tesselator.end();

                    height += 16;
                }
                width += 16;
            }
        }
    }

    @Nonnull
    public static TextureAtlasSprite getStillTexture(Fluid pFluid) {
        ResourceLocation textureLocation = pFluid.getAttributes().getStillTexture();
        if (textureLocation != null) {
            return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLocation);
        } else {
            ResourceLocation waterFallback = Fluids.WATER.getAttributes().getStillTexture();
            return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(waterFallback);
        }
    }

    public static void setFluidColor(int pColor) {
        Color hexColor = new Color(pColor, true);
        float red = hexColor.getRed() / 255f;
        float green = hexColor.getGreen() / 255f;
        float blue = hexColor.getBlue() / 255f;
        float alpha = hexColor.getAlpha() / 255f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }
}

@SuppressWarnings("unused")
interface IDisplayData<T> {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    int getStored();
    int getMaxStored();
    LazyOptional<T> getHandler();
    Component toTextComponent();
}

abstract class DisplayData<T> implements IDisplayData<T> {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public DisplayData(int pX, int pY, int pWidth, int pHeight) {
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

class EnergyDisplayData extends DisplayData<IEnergyStorage> {

    private final AbstractNewBlockEntity blockEntity;

    public EnergyDisplayData(AbstractNewBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
    }

    @Override
    public int getStored() {
        return getHandler().map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public int getMaxStored() {
        return getHandler().map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public LazyOptional<IEnergyStorage> getHandler() {
        LazyOptional<IEnergyStorage> energyStorage = blockEntity.getCapability(CapabilityEnergy.ENERGY);
        if (energyStorage.isPresent()) {
            return energyStorage.cast();
        }
        else return LazyOptional.empty();
    }

    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getMaxStored());
        return stored + "/" + capacity + " FE";
    }
}

class FluidDisplayData extends DisplayData<IFluidHandler> {

    private final AbstractNewBlockEntity blockEntity;

    public FluidDisplayData(AbstractNewBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
    }

    @Override
    public int getStored() {
        return getHandler().map(handler -> handler.getFluidInTank(0).getAmount()).orElse(0);
    }

    @Override
    public int getMaxStored() {
        return getHandler().map(handler -> handler.getTankCapacity(0)).orElse(0);
    }

    @Override
    public LazyOptional<IFluidHandler> getHandler() {
        LazyOptional<IFluidHandler> fluidStorage = blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (fluidStorage.isPresent()) {
            return fluidStorage.cast();
        }
        else return LazyOptional.empty();
    }

    @Override
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String fluidName = "water";
        String stored = numberFormat.format(getStored());
        String capacity = numberFormat.format(getMaxStored());
        return stored + "/" + capacity + " mb " + fluidName;
    }
}
