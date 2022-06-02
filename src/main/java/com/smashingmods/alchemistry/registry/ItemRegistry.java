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

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("Alchemistry") {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(BlockRegistry.ATOMIZER.get());
        }
    };
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ITEM_GROUP);

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> ATOMIZER_ITEM = fromBlock(BlockRegistry.ATOMIZER);
    public static final RegistryObject<Item> COMPACTOR_ITEM = fromBlock(BlockRegistry.COMPACTOR);
    public static final RegistryObject<Item> COMBINER_ITEM = fromBlock(BlockRegistry.COMBINER);
    public static final RegistryObject<Item> DISSOLVER_ITEM = fromBlock(BlockRegistry.DISSOLVER);
    public static final RegistryObject<Item> EVAPORATOR_ITEM = fromBlock(BlockRegistry.EVAPORTOR);
    public static final RegistryObject<Item> LIQIUFIER_ITEM = fromBlock(BlockRegistry.LIQUIFIER);
    public static final RegistryObject<Item> FISSION_CONTROLLER_ITEM = fromBlock(BlockRegistry.FISSION_CONTROLLER);
    public static final RegistryObject<Item> FISSION_CASING_ITEM = fromBlock(BlockRegistry.FISSION_CASING_BLOCK);
    public static final RegistryObject<Item> FISSION_CORE_ITEM = fromBlock(BlockRegistry.FISSION_CORE_BLOCK);
    public static final RegistryObject<Item> FUSION_CONTROLLER_ITEM = fromBlock(BlockRegistry.FUSION_CONTROLLER);
    public static final RegistryObject<Item> FUSION_CASING_ITEM = fromBlock(BlockRegistry.FUSION_CASING_BLOCK);
    public static final RegistryObject<Item> FUSION_CORE_ITEM = fromBlock(BlockRegistry.FUSION_CORE_BLOCK);

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
