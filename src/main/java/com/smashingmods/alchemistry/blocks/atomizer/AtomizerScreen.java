package com.smashingmods.alchemistry.blocks.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.api.client.BaseScreen;
import com.smashingmods.alchemistry.api.client.CapabilityEnergyDisplayWrapper;
import com.smashingmods.alchemistry.api.client.CapabilityFluidDisplayWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AtomizerScreen extends BaseScreen<AtomizerContainer> {
    private final AtomizerBlockEntity atomizerBlockEntity;
    private static final ResourceLocation textureResourceLocation = new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png");

    public AtomizerScreen(AtomizerContainer screenContainer, Inventory pInventory, Component pName) {
        super(screenContainer, pInventory, pName, textureResourceLocation);
        this.atomizerBlockEntity = (AtomizerBlockEntity) getMenu().baseBlockEntity;
        displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, atomizerBlockEntity));
        displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, screenContainer, atomizerBlockEntity));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        Objects.requireNonNull(this.minecraft).textureManager.bindForSetup(textureResourceLocation);
        int x = (this.width - this.getXSize()) / 2;
        int y = (this.height - this.getYSize()) / 2;

        if (atomizerBlockEntity.progressTicks > 0) {
            int barScaled = this.getBarScaled(28, atomizerBlockEntity.progressTicks, Config.ATOMIZER_TICKS_PER_OPERATION.get());
            this.drawRightArrow(pPoseStack, x + 79, y + 63, barScaled);
        }
    }
}
