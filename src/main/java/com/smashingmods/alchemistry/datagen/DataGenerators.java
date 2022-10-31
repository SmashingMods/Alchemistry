package com.smashingmods.alchemistry.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        generator.addProvider(pEvent.includeServer(), new RecipeProvider(generator));
        generator.addProvider(pEvent.includeClient(), new BlockStateGenerator(generator, pEvent.getExistingFileHelper()));
        generator.addProvider(pEvent.includeServer(), new LootTableProvider(generator));
        generator.addProvider(pEvent.includeServer(), new BlockTagProvider(generator, pEvent.getExistingFileHelper()));
        generator.addProvider(pEvent.includeClient(), new LocalizationGenerator(generator, "en_us"));
    }
}

