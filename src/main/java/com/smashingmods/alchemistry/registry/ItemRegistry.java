package com.smashingmods.alchemistry.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class ItemRegistry {

    public static final CreativeModeTab MACHINE_TAB = new CreativeModeTab("Alchemistry") {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(BlockRegistry.ATOMIZER.get());
        }
    };

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(MACHINE_TAB);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static <B extends Block> void fromBlock(RegistryObject<B> block) {
        ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    public static void register(IEventBus eventBus) {
        BlockRegistry.BLOCKS.getEntries().forEach(ItemRegistry::fromBlock);
        ITEMS.register(eventBus);
    }
}
