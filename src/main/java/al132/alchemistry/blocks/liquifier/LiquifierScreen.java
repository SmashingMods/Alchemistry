package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Config;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LiquifierScreen extends ABaseScreen<LiquifierContainer> {
    private LiquifierTile tile;
    private static String path = "textures/gui/liquifier_gui.png";
    private static ResourceLocation texture = new ResourceLocation(Alchemistry.MODID, path);

    public LiquifierScreen(LiquifierContainer container, Inventory inv, Component name) {
        super(Alchemistry.MODID, container, inv, name, path);
        this.tile = (LiquifierTile) container.tile;
        this.displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile));
        this.displayData.add(new CapabilityFluidDisplayWrapper(122, 40, 16, 60, tile));
    }

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(ms, partialTicks, mouseX, mouseY);
        this.minecraft.textureManager.bindForSetup(this.texture);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;
        if (tile.progressTicks > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.LIQUIFIER_TICKS_PER_OPERATION.get());
            this.drawRightArrow(ms, i + 79, j + 63, k);
            //this.blit(ms, i + 79, j + 63, 175, 0, k, 9);
        }
    }
}
