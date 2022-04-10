package com.smashingmods.alchemistry.blocks.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.network.CombinerButtonPkt;
import com.smashingmods.alchemistry.network.Messages;
import com.smashingmods.alchemylib.client.BaseScreen;
import com.smashingmods.alchemylib.client.CapabilityEnergyDisplayWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CombinerScreen extends BaseScreen<CombinerContainer> {

    private Button toggleRecipeLock;// GuiButton
    private Button pauseButton;//: GuiButton
    private CombinerTile tile;
    private CombinerContainer container;
    //private boolean paused;
    //private boolean recipeLocked;

    public CombinerScreen(CombinerContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, "textures/gui/combiner_gui.png");
        this.tile = (CombinerTile) screenContainer.tile;
        //this.paused = tile.paused;
        //this.recipeLocked = tile.recipeIsLocked;
        this.container = screenContainer;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(8, 8, 16, 60, tile));
    }

    @Override
    protected void init() {
        super.init();
        toggleRecipeLock = new Button(this.getGuiLeft() + 30, this.getGuiTop() + 75, 80, 20, new TextComponent(""),
                press -> {
                    Messages.sendToServer(new CombinerButtonPkt(this.tile.getBlockPos(), true, false));
                    //this.recipeLocked = !this.recipeLocked;
                });
        this.addWidget(toggleRecipeLock);
        pauseButton = new Button(this.getGuiLeft() + 30, this.getGuiTop() + 100, 80, 20, new TextComponent(""),
                press -> {
                   // this.paused = !this.paused;
                    Messages.sendToServer(new CombinerButtonPkt(this.tile.getBlockPos(), false, true));
                });
        this.addWidget(pauseButton);
    }

    public void updateButtonStrings() {
        if (this.tile.recipeIsLocked)
            toggleRecipeLock.setMessage(new TextComponent(I18n.get("block.combiner.unlock_recipe")));
        else toggleRecipeLock.setMessage(new TextComponent(I18n.get("block.combiner.lock_recipe")));

        if (this.tile.paused) pauseButton.setMessage(new TextComponent(I18n.get("block.combiner.resume")));
        else pauseButton.setMessage(new TextComponent(I18n.get("block.combiner.pause")));
    }

    @Override
    public void render(PoseStack ps, int mouseX, int mouseY, float partialTicks) {
        super.render(ps, mouseX, mouseY, partialTicks);
        updateButtonStrings();
        toggleRecipeLock.renderButton(ps, mouseX, mouseY, 0.0f);
        pauseButton.renderButton(ps, mouseX, mouseY, 0.0f);

        if (!tile.clientRecipeTarget.getStackInSlot(0).isEmpty()) {
            this.drawItemStack(ps, tile.clientRecipeTarget.getStackInSlot(0), getGuiLeft() + 140, getGuiTop() + 5, I18n.get("block.combiner.target"));
        }
    }


    public void drawItemStack(PoseStack ps, ItemStack stack, int x, int y, String text) {
        //RenderHelper.enableStandardItemLighting();//.enableGUIStandardItemLighting();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        ps.translate(0.0f, 0.0f, 32.0f);
        //this.setBlitOffset(200);// = 200;
        //this.itemRenderer.blitOffset = 200.0f;
        this.itemRenderer.renderGuiItem(stack, x, y);
        this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y + 5, text);
        //this.setBlitOffset(0);// = 0;
        //this.itemRenderer.blitOffset = 0.0f;
    }
}