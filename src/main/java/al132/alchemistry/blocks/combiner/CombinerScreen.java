package al132.alchemistry.blocks.combiner;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.network.CombinerButtonPkt;
import al132.alchemistry.network.NetworkHandler;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CombinerScreen extends ABaseScreen<CombinerContainer> {

    private Button toggleRecipeLock;// GuiButton
    private Button pauseButton;//: GuiButton
    private CombinerTile tile;
    private CombinerContainer container;

    public CombinerScreen(CombinerContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, "textures/gui/combiner_gui.png");
        this.tile = (CombinerTile) screenContainer.tile;
        this.container = screenContainer;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(8, 8, 16, 60, screenContainer::getEnergy));
    }

    @Override
    protected void init() {
        super.init();
        toggleRecipeLock = new Button(this.guiLeft + 30, this.guiTop + 75, 80, 20, new StringTextComponent(""),
                press -> NetworkHandler.sendToServer(new CombinerButtonPkt(this.tile.getPos(), true, false)));
        this.addButton(toggleRecipeLock);
        pauseButton = new Button(this.guiLeft + 30, this.guiTop + 100, 80, 20, new StringTextComponent(""),
                press -> NetworkHandler.sendToServer(new CombinerButtonPkt(this.tile.getPos(), false, true)));
        this.addButton(pauseButton);
    }

    public void updateButtonStrings() {
        if (tile.recipeIsLocked)
            toggleRecipeLock.setMessage(new StringTextComponent(I18n.format("block.combiner.unlock_recipe")));
        else toggleRecipeLock.setMessage(new StringTextComponent(I18n.format("block.combiner.lock_recipe")));

        if (tile.paused) pauseButton.setMessage(new StringTextComponent(I18n.format("block.combiner.resume")));
        else pauseButton.setMessage(new StringTextComponent(I18n.format("block.combiner.pause")));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(ms, f, mouseX, mouseY);
        updateButtonStrings();
        toggleRecipeLock.renderButton(ms, mouseX, mouseY, 0.0f);
        pauseButton.renderButton(ms, mouseX, mouseY, 0.0f);

        if (!tile.clientRecipeTarget.getStackInSlot(0).isEmpty()) {
            this.drawItemStack(tile.clientRecipeTarget.getStackInSlot(0), guiLeft + 140, guiTop + 5, I18n.format("block.combiner.target"));
        }
    }


    public void drawItemStack(ItemStack stack, int x, int y, String text) {
        RenderHelper.enableStandardItemLighting();//.enableGUIStandardItemLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.translatef(0.0f, 0.0f, 32.0f);
        this.setBlitOffset(200);// = 200;
        this.itemRenderer.zLevel = 200.0f;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y + 5, text);
        this.setBlitOffset(0);// = 0;
        this.itemRenderer.zLevel = 0.0f;
    }
}
