package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class BlockLootTables extends BlockLoot {

    @Override
    protected void addTables() {
        this.dropSelf(BlockRegistry.ATOMIZER.get());
        this.dropSelf(BlockRegistry.COMPACTOR.get());
        this.dropSelf(BlockRegistry.COMBINER.get());
        this.dropSelf(BlockRegistry.DISSOLVER.get());
        this.dropSelf(BlockRegistry.EVAPORTOR.get());
        this.dropSelf(BlockRegistry.LIQUIFIER.get());
        this.dropSelf(BlockRegistry.FISSION_CONTROLLER.get());
        this.dropSelf(BlockRegistry.FISSION_CASING_BLOCK.get());
        this.dropSelf(BlockRegistry.FISSION_CORE_BLOCK.get());
        this.dropSelf(BlockRegistry.FUSION_CONTROLLER.get());
        this.dropSelf(BlockRegistry.FUSION_CASING_BLOCK.get());
        this.dropSelf(BlockRegistry.FUSION_CORE_BLOCK.get());
    }

    @Override
    @Nonnull
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
