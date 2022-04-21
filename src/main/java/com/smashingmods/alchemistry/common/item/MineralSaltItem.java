package com.smashingmods.alchemistry.common.item;

import com.smashingmods.alchemistry.registry.ItemRegistry;
import com.smashingmods.alchemistry.registry.Registry;
import net.minecraft.world.item.Item;

public class MineralSaltItem extends Item {
    public MineralSaltItem() {
        super(new Item.Properties().tab(ItemRegistry.ITEM_GROUP));
    }
}
