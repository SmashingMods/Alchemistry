package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.Alchemistry;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import al132.alib.client.CapabilityFluidDisplayWrapper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class AtomizerScreen extends ABaseScreen<AtomizerContainer> {
    public AtomizerScreen(AtomizerContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, "textures/gui/atomizer_gui.png");
        displayData.add(new CapabilityEnergyDisplayWrapper(7, 10, 16, 60, screenContainer::getEnergy));
        displayData.add(new CapabilityFluidDisplayWrapper(48, 40, 16, 60, screenContainer::getTank));
    }
}
