package com.smashingmods.alchemistry.common.block.fusion;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class FusionCoreBlock extends Block {
    public FusionCoreBlock() {
        super( Block.Properties.of(Material.METAL).strength(2.0f));
    }
}
