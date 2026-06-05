package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.Set;

public class BlockLootTables extends BlockLootSubProvider {

    public BlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        BlockRegistry.BLOCKS.getEntries().stream().map(DeferredHolder::get).forEach(this::dropSelf);
    }

    @Override
    @Nonnull
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries().stream().<Block>map(DeferredHolder::get)::iterator;
    }
}
