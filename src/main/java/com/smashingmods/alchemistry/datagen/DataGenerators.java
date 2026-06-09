package com.smashingmods.alchemistry.datagen;

import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.datagen.book.AlchemistryBook;
import com.smashingmods.alchemistry.datagen.book.AlchemistryMultiblockProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        ExistingFileHelper fileHelper = pEvent.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = pEvent.getLookupProvider();

        // Modonomicon collects the guidebook's name/description/title/text strings into this cache as the book
        // is generated; LocalizationGenerator drains it into en_us.json so a single lang provider owns the file.
        LanguageProviderCache enUsCache = new LanguageProviderCache("en_us");

        generator.addProvider(pEvent.includeServer(), new RecipeGenerator.Runner(packOutput, lookupProvider));
        generator.addProvider(pEvent.includeClient(), new BlockStateGenerator(packOutput, pEvent.getExistingFileHelper()));
        generator.addProvider(pEvent.includeServer(), LootTableGenerator.create(packOutput, lookupProvider));
        generator.addProvider(pEvent.includeServer(), new BlockTagGenerator(packOutput, lookupProvider, fileHelper));
        generator.addProvider(pEvent.includeServer(), NeoBookProvider.of(pEvent, new AlchemistryBook(Alchemistry.MODID, enUsCache)));
        generator.addProvider(pEvent.includeServer(), new AlchemistryMultiblockProvider(packOutput));
        // Registered after the book provider so the cache is fully populated when this provider runs (the data
        // generator runs providers sequentially in registration order).
        generator.addProvider(pEvent.includeClient(), new LocalizationGenerator(packOutput, enUsCache));
    }
}

