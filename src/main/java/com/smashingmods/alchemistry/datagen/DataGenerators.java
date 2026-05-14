package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Alchemistry.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        ExistingFileHelper fileHelper = pEvent.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = pEvent.getLookupProvider();

        generator.addProvider(pEvent.includeServer(), new RecipeGenerator(packOutput, lookupProvider));
        generator.addProvider(pEvent.includeClient(), new BlockStateGenerator(packOutput, fileHelper));
        generator.addProvider(pEvent.includeServer(), LootTableGenerator.create(packOutput, lookupProvider));
        generator.addProvider(pEvent.includeServer(), new BlockTagGenerator(packOutput, lookupProvider, fileHelper));
        generator.addProvider(pEvent.includeClient(), new LocalizationGenerator(packOutput));
    }
}
