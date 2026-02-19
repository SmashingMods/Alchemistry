package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TabRegistry {

    public static final DeferredRegister<CreativeModeTab> REGISTRY_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Alchemistry.MODID);
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> MACHINE_TAB;

    public static void register(IEventBus pEventBus) {
        MACHINE_TAB = REGISTRY_TABS.register("machine_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> BlockRegistry.ATOMIZER.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.alchemistry"))
            .displayItems((pParameters, pOutput) -> ItemRegistry.getItems().forEach(pOutput::accept))
            .build());
        REGISTRY_TABS.register(pEventBus);
    }
}
