package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Config;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AtomizerScreen extends ABaseScreen<AtomizerContainer> {
    private AtomizerTile tile;
    private static String path = "textures/gui/atomizer_gui.png";
    private static ResourceLocation texture = new ResourceLocation(Alchemistry.MODID, path);

    public AtomizerScreen(AtomizerContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, path);
        this.tile = (AtomizerTile) getMenu().tile;
        displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile));
        displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, tile));
    }

    @Override
    public void render(PoseStack ps, int mouseX, int mouseY, float partialTicks) {
        super.render(ps, mouseX, mouseY, partialTicks);
        this.minecraft.textureManager.bindForSetup(this.texture);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;
        if (tile.progressTicks > 0) {//.getProgressTicks() > 0) {
            int k = this.getBarScaled(28, tile.progressTicks, Config.ATOMIZER_TICKS_PER_OPERATION.get());
            //this.blit(ps, i + 79, j + 63, 175, 0, k, 9);
            this.drawRightArrow(ps, i + 79, j + 63, k);
        }
    }
}
