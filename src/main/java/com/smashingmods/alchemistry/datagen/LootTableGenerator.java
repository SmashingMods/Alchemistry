package com.smashingmods.alchemistry.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LootTableGenerator {

    public static LootTableProvider create(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider) {
        return new LootTableProvider(pOutput, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)),
                pProvider);
    }
}
