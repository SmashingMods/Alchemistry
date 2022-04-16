package com.smashingmods.alchemistry.block.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.container.BaseScreen;
import com.smashingmods.alchemistry.api.container.CapabilityEnergyDisplayWrapper;
import com.smashingmods.alchemistry.api.container.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LiquifierScreen extends BaseScreen<LiquifierContainer> {

    private final LiquifierBlockEntity blockEntity;
    private static final ResourceLocation textureResourceLocation = new ResourceLocation(Alchemistry.MODID, "textures/gui/liquifier_gui.png");

    public LiquifierScreen(LiquifierContainer pContainer, Inventory pInventory, Component pName) {
        super(pContainer, pInventory, pName, textureResourceLocation);
        this.blockEntity = (LiquifierBlockEntity) getMenu().blockEntity;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, pContainer));
        this.displayData.add(new CapabilityFluidDisplayWrapper(122, 40, 16, 60, pContainer));
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(ms, partialTicks, mouseX, mouseY);
        Objects.requireNonNull(this.minecraft).textureManager.bindForSetup(textureResourceLocation);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;
        if (blockEntity.progressTicks > 0) {
            int k = this.getBarScaled(28, blockEntity.progressTicks, Config.LIQUIFIER_TICKS_PER_OPERATION.get());
            this.drawRightArrow(ms, i + 79, j + 63, k);
            //this.blit(ms, i + 79, j + 63, 175, 0, k, 9);
        }
    }
}
