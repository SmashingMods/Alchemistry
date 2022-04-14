package com.smashingmods.alchemistry.item;

import com.smashingmods.alchemistry.Registry;
import net.minecraft.world.item.Item;

public class SlotFillerItem extends Item {

    public SlotFillerItem() {
        super(new Item.Properties().tab(Registry.ITEM_GROUP));
    }
}
