package com.smashingmods.alchemistry.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class ItemRegistry {

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static <B extends Block> void fromBlock(RegistryObject<B> block) {
        ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    public static List<Item> getItems() {
        return ITEMS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static void register(IEventBus eventBus) {
        BlockRegistry.BLOCKS.getEntries().forEach(ItemRegistry::fromBlock);
        ITEMS.register(eventBus);
    }
}
