package com.smashingmods.alchemistry.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        ExistingFileHelper fileHelper = pEvent.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = pEvent.getLookupProvider();

        generator.addProvider(pEvent.includeServer(), new RecipeGenerator(packOutput, lookupProvider));
        generator.addProvider(pEvent.includeClient(), new BlockStateGenerator(packOutput, pEvent.getExistingFileHelper()));
        generator.addProvider(pEvent.includeServer(), LootTableGenerator.create(packOutput, lookupProvider));
        generator.addProvider(pEvent.includeServer(), new BlockTagGenerator(packOutput, lookupProvider, fileHelper));
        generator.addProvider(pEvent.includeClient(), new LocalizationGenerator(packOutput));
    }
}

