package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.AlchemistryBlockItem;
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
        ITEMS.registerItem(block.getId().getPath(), properties -> new AlchemistryBlockItem(block.get(), properties), blockItemProperties());
    }

    /**
     * The {@link Item.Properties} every Alchemistry block-item registers with. Without
     * {@code useBlockDescriptionPrefix} a BlockItem bakes an {@code item.alchemistry.*} description id (since
     * 1.21.2), but the generated lang only defines {@code block.alchemistry.*} keys, so the prefix is what makes
     * the names resolve.
     */
    static Item.Properties blockItemProperties() {
        return new Item.Properties().useBlockDescriptionPrefix();
    }

    public static List<Item> getItems() {
        return ITEMS.getEntries().stream().<Item>map(DeferredHolder::get).toList();
    }

    public static void register(IEventBus eventBus) {
        BlockRegistry.BLOCKS.getEntries().forEach(ItemRegistry::fromBlock);
        ITEMS.register(eventBus);
    }
}
