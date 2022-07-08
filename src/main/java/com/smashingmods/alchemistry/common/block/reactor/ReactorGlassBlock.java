package com.smashingmods.alchemistry.common.block.reactor;

import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReactorGlassBlock extends AbstractGlassBlock {
    public ReactorGlassBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(2.0f));
    }
}
