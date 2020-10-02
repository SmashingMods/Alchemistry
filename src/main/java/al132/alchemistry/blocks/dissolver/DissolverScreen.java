package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.Alchemistry;
import al132.alib.client.ABaseScreen;
import al132.alib.client.CapabilityEnergyDisplayWrapper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DissolverScreen extends ABaseScreen<DissolverContainer> {


    public DissolverScreen(DissolverContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(Alchemistry.data, screenContainer, inv, name, "textures/gui/dissolver_gui.png");
        this.displayData.add(new CapabilityEnergyDisplayWrapper(13,31,16,60,screenContainer::getEnergy));
    }
}
