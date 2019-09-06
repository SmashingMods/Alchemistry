package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Config;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class LiquifierScreen extends ABaseScreen<LiquifierContainer> {
    private LiquifierTile tile;
    private static String path = "textures/gui/liquifier_gui.png";
    private static ResourceLocation texture = new ResourceLocation(Alchemistry.data.MODID, path);

    public LiquifierScreen(LiquifierContainer container, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, container, inv, name, path);
        this.tile = (LiquifierTile) container.tile;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, container::getEnergy));
        this.displayData.add(new CapabilityFluidDisplayWrapper(122, 40, 16, 60, container::getTank));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.minecraft.textureManager.bindTexture(this.texture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.LIQUIFIER_TICKS_PER_OPERATION.get());
            this.blit(i + 79, j + 63, 175, 0, k, 9);
        }
    }
}
