package al132.alchemistry.blocks.fission;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Config;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;


public class FissionScreen extends ABaseScreen<FissionContainer> {

    private String statusText = "";
    private FissionTile tile;
    private static String path = "textures/gui/fission_gui.png";
    public static ResourceLocation texture = new ResourceLocation(Alchemistry.MODID, path);

    public FissionScreen(FissionContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, path);
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile));
        tile = (FissionTile) screenContainer.tile;
    }


    @Override
    protected void renderBg(PoseStack ms, float f, int mouseX, int mouseY) {
        super.renderBg(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindForSetup(texture);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks , Config.FISSION_TICKS_PER_OPERATION.get());
            this.drawRightArrow(ms, i + 79, j + 63, k);
        }
    }


    public void updateStatus() {
        if (tile.isValidMultiblock) statusText = "";
        else statusText = I18n.get("alchemistry.fission.invalid_multiblock");
    }


    @Override
    protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
        super.renderLabels(ms, mouseX, mouseY);
        //super.func_230450_a_(ms, f, mouseX, mouseY);
        updateStatus();
        this.font.drawShadow(ms, statusText, 30.0f, 100.0f, Color.WHITE.getRGB());
    }
}
