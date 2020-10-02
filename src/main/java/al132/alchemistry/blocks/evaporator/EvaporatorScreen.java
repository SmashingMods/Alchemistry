package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.Alchemistry;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class EvaporatorScreen extends ABaseScreen<EvaporatorContainer> {

    private EvaporatorTile tile;
    public static final ResourceLocation path = new ResourceLocation(Alchemistry.data.MODID, "textures/gui/evaporator_gui.png");

    public EvaporatorScreen(EvaporatorContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, "textures/gui/evaporator_gui.png");
        this.tile = (EvaporatorTile) screenContainer.tile;
        this.displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, screenContainer::getFluids));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindTexture(path);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, tile.calculateProcessingTime());
            this.blit(ms, i + 79, j + 63, 175, 0, k, 9);
        }
    }
}
