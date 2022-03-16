package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.Alchemistry;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class EvaporatorScreen extends ABaseScreen<EvaporatorContainer> {

    private EvaporatorTile tile;
    public static final ResourceLocation path = new ResourceLocation(Alchemistry.MODID, "textures/gui/evaporator_gui.png");

    public EvaporatorScreen(EvaporatorContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, "textures/gui/evaporator_gui.png");
        this.tile = (EvaporatorTile) screenContainer.tile;
        this.displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, getMenu()));
    }



    @Override
    protected void renderBg(PoseStack ms, float f, int mouseX, int mouseY) {
        super.renderBg(ms, f, mouseX, mouseY);
        this.getMinecraft().textureManager.bindForSetup(path);
        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;
        if (getMenu().getProgressTicks() > 0) {
            int k = this.getBarScaled(28, getMenu().getProgressTicks(), tile.calculateProcessingTime());
            this.blit(ms, i + 79, j + 63, 175, 0, k, 9);
        }
    }
}
