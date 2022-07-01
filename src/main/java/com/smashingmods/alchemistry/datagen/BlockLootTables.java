package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class BlockLootTables extends BlockLoot {

    @Override
    protected void addTables() {
        BlockRegistry.BLOCKS.getEntries().forEach(registryObject -> dropSelf(registryObject.get()));
    }

    @Override
    @Nonnull
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
