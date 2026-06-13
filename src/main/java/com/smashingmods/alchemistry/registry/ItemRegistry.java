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

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static <B extends Block> void fromBlock(DeferredHolder<B, ? extends Block> block) {
        ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    public static List<? extends Item> getItems() {
        return ITEMS.getEntries().stream().map(DeferredHolder::get).toList();
    }

    public static void register(IEventBus eventBus) {
        BlockRegistry.BLOCKS.getEntries().forEach(ItemRegistry::fromBlock);
        ITEMS.register(eventBus);
    }
}
