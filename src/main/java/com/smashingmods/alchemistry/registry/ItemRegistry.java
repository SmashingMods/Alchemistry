package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.item.CondensedMilkItem;
import com.smashingmods.alchemistry.common.item.MineralSaltItem;
import com.smashingmods.alchemistry.common.item.SlotFillerItem;
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

    public static final RegistryObject<Item> NEW_ATOMIZER_ITEM = fromBlock(BlockRegistry.ATOMIZER);
    public static final RegistryObject<Item> NEW_COMBINER_ITEM = fromBlock(BlockRegistry.COMBINER);
    public static final RegistryObject<Item> NEW_DISSOLVER_ITEM = fromBlock(BlockRegistry.DISSOLVER);
    public static final RegistryObject<Item> NEW_EVAPORATOR_ITEM = fromBlock(BlockRegistry.EVAPORTOR);
    public static final RegistryObject<Item> NEW_LIQIUFIER_ITEM = fromBlock(BlockRegistry.LIQUIFIER);
    public static final RegistryObject<Item> NEW_FISSION_CONTROLLER_ITEM = fromBlock(BlockRegistry.FISSION_CONTROLLER);
    public static final RegistryObject<Item> FISSION_CASING_ITEM = fromBlock(BlockRegistry.FISSION_CASING_BLOCK);
    public static final RegistryObject<Item> FISSION_CORE_ITEM = fromBlock(BlockRegistry.FISSION_CORE_BLOCK);
    public static final RegistryObject<Item> NEW_FUSION_CONTROLLER_ITEM = fromBlock(BlockRegistry.FUSION_CONTROLLER);
    public static final RegistryObject<Item> FUSION_CASING_ITEM = fromBlock(BlockRegistry.FUSION_CASING_BLOCK);
    public static final RegistryObject<Item> FUSION_CORE_ITEM = fromBlock(BlockRegistry.FUSION_CORE_BLOCK);

    public static final RegistryObject<Item> SLOT_FILLER_ITEM = ITEMS.register("slot_filler", SlotFillerItem::new);
    public static final RegistryObject<Item> MINERAL_SALT_ITEM = ITEMS.register("mineral_salt", MineralSaltItem::new);
    public static final RegistryObject<Item> CONDENSED_MILK_ITEM = ITEMS.register("condensed_milk", CondensedMilkItem::new);

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
