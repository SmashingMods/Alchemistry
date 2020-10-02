package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Config;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AtomizerScreen extends ABaseScreen<AtomizerContainer> {
    private AtomizerTile tile;
    private static String path = "textures/gui/atomizer_gui.png";
    private static ResourceLocation texture = new ResourceLocation(Alchemistry.data.MODID, path);

    public AtomizerScreen(AtomizerContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, path);
        this.tile = (AtomizerTile) container.tile;
        displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, screenContainer::getEnergy));
        displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, screenContainer::getTank));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(ms, f, mouseX, mouseY);
        this.minecraft.textureManager.bindTexture(this.texture);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.ATOMIZER_TICKS_PER_OPERATION.get());
            this.blit(ms, i + 79, j + 63, 175, 0, k, 9);
        }
    }
}
