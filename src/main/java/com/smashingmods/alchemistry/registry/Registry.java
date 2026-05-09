package com.smashingmods.alchemistry.registry;

import net.neoforged.bus.api.IEventBus;

public class Registry {

    public static void register(IEventBus modEventBus) {
        BlockRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        TabRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);
        RecipeRegistry.register(modEventBus);
    }
}
