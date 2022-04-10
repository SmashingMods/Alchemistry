package com.smashingmods.alchemistry.blocks.fusion;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemylib.client.BaseScreen;
import com.smashingmods.alchemylib.client.CapabilityEnergyDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;


public class FusionScreen extends BaseScreen<FusionContainer> {
    private FusionTile tile;
    private String statusText = "";
    private static String path = "textures/gui/fusion_gui.png";
    public static ResourceLocation texture = new ResourceLocation(Alchemistry.MODID, path);

    public FusionScreen(FusionContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, "textures/gui/fusion_gui.png");
        this.tile = (FusionTile) screenContainer.tile;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile));
    }

    @Override
    protected void renderBg(PoseStack ms, float f, int mouseX, int mouseY) {
        super.renderBg(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindForSetup(texture);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.FUSION_TICKS_PER_OPERATION.get());
            //this.blit(ms, i + 90, j + 82, 175, 0, k, 9);
            this.drawRightArrow(ms, i + 90, j + 82, k);

        }
    }


    public void updateStatus() {
        if (tile.isValidMultiblock) statusText = "";
        else statusText = I18n.get("alchemistry.fusion.invalid_multiblock");
    }

    @Override
    protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
        super.renderLabels(ms, mouseX, mouseY);
        //super.func_230450_a_(ms,f,mouseX, mouseY);
        updateStatus();
        this.font.drawShadow(ms, statusText, 30.0f, 110.0f, Color.WHITE.getRGB());
    }
}
