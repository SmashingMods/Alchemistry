package com.smashingmods.alchemistry.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class ItemRegistry {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static void fromBlock(DeferredHolder<Block, ? extends Block> block) {
        ITEMS.registerItem(block.getId().getPath(), properties -> new BlockItem(block.get(), properties), new Item.Properties());
    }

    public static List<Item> getItems() {
        return ITEMS.getEntries().stream().<Item>map(DeferredHolder::get).toList();
    }

    public static void register(IEventBus eventBus) {
        BlockRegistry.BLOCKS.getEntries().forEach(ItemRegistry::fromBlock);
        ITEMS.register(eventBus);
    }
}
