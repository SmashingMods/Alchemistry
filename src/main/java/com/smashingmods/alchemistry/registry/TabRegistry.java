package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Alchemistry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabRegistry {

    public static CreativeModeTab MACHINE_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register pEvent) {
        MACHINE_TAB = pEvent.registerCreativeModeTab(new ResourceLocation(Alchemistry.MODID, "machine_tab"),
                List.of(),
                List.of(CreativeModeTabs.SPAWN_EGGS),
                builder -> builder
                        .icon(() -> new ItemStack(BlockRegistry.ATOMIZER.get()))
                        .title(Component.translatable("itemGroup.alchemistry")));
    }

    @SubscribeEvent
    public static void addCreativeModeTabs(CreativeModeTabEvent.BuildContents pEvent) {
        if (pEvent.getTab() == MACHINE_TAB) {
            ItemRegistry.getItems().forEach(pEvent::accept);
        }
    }
}
