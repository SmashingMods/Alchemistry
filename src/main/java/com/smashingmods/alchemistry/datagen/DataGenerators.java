package com.smashingmods.alchemistry.datagen;

import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.datagen.book.AlchemistryBook;
import com.smashingmods.alchemistry.datagen.book.AlchemistryMultiblockProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DataGenerators {

    // 1.21.4 dropped GatherDataEvent#includeServer/#includeClient/#getExistingFileHelper: providers are now
    // added unconditionally via the event and the client/server split is driven by the run type (this mod's
    // single "clientData --all" run fires GatherDataEvent.Client and runs every provider registered here).
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = pEvent.getLookupProvider();

        // Modonomicon collects the guidebook's name/description/title/text strings into this cache as the book
        // is generated; LocalizationGenerator drains it into en_us.json so a single lang provider owns the file.
        LanguageProviderCache enUsCache = new LanguageProviderCache("en_us");

        pEvent.addProvider(new RecipeGenerator.Runner(packOutput, lookupProvider));
        pEvent.addProvider(new BlockStateGenerator(packOutput));
        pEvent.addProvider(LootTableGenerator.create(packOutput, lookupProvider));
        pEvent.addProvider(new BlockTagGenerator(packOutput, lookupProvider));
        pEvent.addProvider(new GameTestStructureProvider(packOutput));
        pEvent.addProvider(NeoBookProvider.of(pEvent, new AlchemistryBook(Alchemistry.MODID, enUsCache)));
        pEvent.addProvider(new AlchemistryMultiblockProvider(packOutput));
        // Registered after the book provider so the cache is fully populated when this provider runs (the data
        // generator runs providers sequentially in registration order).
        pEvent.addProvider(new LocalizationGenerator(packOutput, enUsCache));
    }
}

