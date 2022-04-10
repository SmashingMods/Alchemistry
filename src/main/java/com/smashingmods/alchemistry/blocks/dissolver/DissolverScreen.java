package com.smashingmods.alchemistry.blocks.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.client.BaseScreen;
import com.smashingmods.alchemylib.client.CapabilityEnergyDisplayWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DissolverScreen extends BaseScreen<DissolverContainer> {


    public DissolverScreen(DissolverContainer screenContainer, Inventory inv, Component name) {
        super(Alchemistry.MODID, screenContainer, inv, name, "textures/gui/dissolver_gui.png");
        this.displayData.add(new CapabilityEnergyDisplayWrapper(13, 31, 16, 60, screenContainer.tile));
    }
}
