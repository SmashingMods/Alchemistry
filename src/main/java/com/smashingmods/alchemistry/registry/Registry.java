package com.smashingmods.alchemistry.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Registry {

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        TabRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);
        RecipeRegistry.register(modEventBus);
    }
}
