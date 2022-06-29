package com.smashingmods.alchemistry.common.block.fusion;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class FusionCasingBlock extends Block {

    public FusionCasingBlock() {
        super( Properties.of(Material.METAL).strength(2.0f));
    }
}
