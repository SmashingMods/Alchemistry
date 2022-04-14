package com.smashingmods.alchemistry.blocks.evaporator;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.client.BaseScreen;
import com.smashingmods.alchemistry.api.client.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class EvaporatorScreen extends BaseScreen<EvaporatorContainer> {

    private EvaporatorBlockEntity evaporatorBlockEntity;
    public static final ResourceLocation textureResourceLocation = new ResourceLocation(Alchemistry.MODID, "textures/gui/evaporator_gui.png");

    public EvaporatorScreen(EvaporatorContainer screenContainer, Inventory inv, Component name) {
        super(screenContainer, inv, name, textureResourceLocation);
        this.evaporatorBlockEntity = (EvaporatorBlockEntity) screenContainer.blockEntity;
        this.displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, screenContainer, evaporatorBlockEntity));
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float f, int mouseX, int mouseY) {
        super.renderBg(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindForSetup(textureResourceLocation);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;

        if (evaporatorBlockEntity.progressTicks > 0) {
            int k = this.getBarScaled(28, evaporatorBlockEntity.progressTicks, evaporatorBlockEntity.calculateProcessingTime());
            this.drawRightArrow(ms, i + 79, j + 63, k);
        }
    }
}
