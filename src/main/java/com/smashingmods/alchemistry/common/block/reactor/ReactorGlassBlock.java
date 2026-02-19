package com.smashingmods.alchemistry.common.block.reactor;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReactorGlassBlock extends TransparentBlock {
    public ReactorGlassBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(2.0f));
    }
}
