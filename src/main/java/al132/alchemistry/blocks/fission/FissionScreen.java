package al132.alchemistry.blocks.fission;

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

public class FissionScreen extends ABaseScreen<FissionContainer> {

    private String statusText = "";
    private FissionTile tile;
    private static String path = "textures/gui/fission_gui.png";
    public static ResourceLocation texture = new ResourceLocation(Alchemistry.data.MODID, path);

    public FissionScreen(FissionContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, path);
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, screenContainer::getEnergy));
        tile = (FissionTile) screenContainer.tile;
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindTexture(texture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.FISSION_TICKS_PER_OPERATION.get());
            this.blit(ms, i + 79, j + 63, 175, 0, k, 9);
        }
    }


    public void updateStatus() {
        if (tile.isValidMultiblock) statusText = "";
        else statusText = I18n.format("alchemistry.fission.invalid_multiblock");
    }


    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(ms, mouseX, mouseY);
        //super.func_230450_a_(ms, f, mouseX, mouseY);
        updateStatus();
        this.font.drawStringWithShadow(ms, statusText, 30.0f, 100.0f, Color.WHITE.getRGB());
    }
}
