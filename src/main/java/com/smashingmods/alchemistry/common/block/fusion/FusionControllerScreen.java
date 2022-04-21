package com.smashingmods.alchemistry.common.block.fusion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryScreen;
import com.smashingmods.alchemistry.api.container.DisplayData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FusionControllerScreen extends AbstractAlchemistryScreen<FusionControllerMenu> {

    protected final List<DisplayData> displayData = new ArrayList<>();

    public FusionControllerScreen(FusionControllerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
