package al132.alchemistry.blocks.fusion;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Config;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class FusionScreen extends ABaseScreen<FusionContainer> {
    private FusionTile tile;
    private String statusText = "";
    private static String path = "textures/gui/fusion_gui.png";
    public static ResourceLocation texture = new ResourceLocation(Alchemistry.data.MODID, path);

    public FusionScreen(FusionContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, "textures/gui/fusion_gui.png");
        this.tile = (FusionTile) screenContainer.tile;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, screenContainer::getEnergy));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindTexture(texture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.FUSION_TICKS_PER_OPERATION.get());
            this.blit(ms, i + 90, j + 82, 175, 0, k, 9);
        }
    }


    public void updateStatus() {
        if (tile.isValidMultiblock) statusText = "";
        else statusText = I18n.format("alchemistry.fusion.invalid_multiblock");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(ms, mouseX, mouseY);
        //super.func_230450_a_(ms,f,mouseX, mouseY);
        updateStatus();
        this.font.drawStringWithShadow(ms, statusText, 30.0f, 110.0f, Color.WHITE.getRGB());
    }
}
