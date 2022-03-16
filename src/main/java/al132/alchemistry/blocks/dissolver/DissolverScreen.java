package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.Alchemistry;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class DissolverScreen extends ABaseScreen<DissolverContainer> {


    public DissolverScreen(DissolverContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, "textures/gui/dissolver_gui.png");
        this.displayData.add(new CapabilityEnergyDisplayWrapper(13, 31, 16, 60, getMenu()));
    }
}
