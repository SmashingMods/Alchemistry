//package com.smashingmods.alchemistry.common.block.oldblocks.fusion;
//
//import com.smashingmods.alchemistry.Alchemistry;
//import com.smashingmods.alchemistry.Config;
//import com.smashingmods.alchemistry.common.block.oldblocks.container.BaseScreen;
//import com.smashingmods.alchemistry.common.block.oldblocks.container.CapabilityEnergyDisplayWrapper;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.resources.language.I18n;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Inventory;
//import org.jetbrains.annotations.NotNull;
//
//import java.awt.*;
//
//public class FusionScreen extends BaseScreen<FusionContainer> {
//    private FusionBlockEntity fusionBlockEntity;
//    private String statusText = "";
//    public static ResourceLocation textureResourceLocation = new ResourceLocation(Alchemistry.MODID, "textures/gui/fusion_gui.png");
//
//    public FusionScreen(FusionContainer pContainer, Inventory pInventory, Component pName) {
//        super(pContainer, pInventory, pName, textureResourceLocation);
//        this.fusionBlockEntity = (FusionBlockEntity) getMenu().blockEntity;
//        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, pContainer));
//    }
//
//    @Override
//    protected void renderBg(@NotNull PoseStack ms, float f, int mouseX, int mouseY) {
//        super.renderBg(ms, f, mouseX, mouseY);
//        this.getMinecraft().textureManager.bindForSetup(textureResourceLocation);
//        int i = (this.width - this.getXSize()) / 2;
//        int j = (this.height - this.getYSize()) / 2;
//        if (fusionBlockEntity.progressTicks > 0) {
//            int k = this.getBarScaled(28, fusionBlockEntity.progressTicks, Config.FUSION_TICKS_PER_OPERATION.get());
//            //this.blit(ms, i + 90, j + 82, 175, 0, k, 9);
//            this.drawRightArrow(ms, i + 90, j + 82, k);
//
//        }
//    }
//
//
//    public void updateStatus() {
//        if (fusionBlockEntity.isValidMultiblock) statusText = "";
//        else statusText = I18n.get("alchemistry.container.fusion_controller.invalid_multiblock");
//    }
//
//    @Override
//    protected void renderLabels(@NotNull PoseStack ms, int mouseX, int mouseY) {
//        super.renderLabels(ms, mouseX, mouseY);
//        //super.func_230450_a_(ms,f,mouseX, mouseY);
//        updateStatus();
//        this.font.drawShadow(ms, statusText, 30.0f, 110.0f, Color.WHITE.getRGB());
//    }
//}
