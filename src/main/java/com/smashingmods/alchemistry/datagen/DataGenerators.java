package com.smashingmods.alchemistry.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        ExistingFileHelper fileHelper = pEvent.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = pEvent.getLookupProvider();

        generator.addProvider(pEvent.includeServer(), new RecipeGenerator(packOutput));
        generator.addProvider(pEvent.includeClient(), new BlockStateGenerator(packOutput, pEvent.getExistingFileHelper()));
        generator.addProvider(pEvent.includeServer(), LootTableGenerator.create(packOutput));
        generator.addProvider(pEvent.includeServer(), new BlockTagGenerator(packOutput, lookupProvider, fileHelper));
        generator.addProvider(pEvent.includeClient(), new LocalizationGenerator(packOutput));
    }
}

