package com.smashingmods.alchemistry.common.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Implemented by blocks that contribute lines to their block-item's tooltip. 1.21.4 let a block override
 * {@code BlockBehaviour#appendHoverText} directly; 1.21.5 removed that hook (tooltips are now an item concern),
 * so {@link AlchemistryBlockItem} forwards to this method for any block that implements it. The signature mirrors
 * 1.21.5's {@link Item#appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer, TooltipFlag)}.
 */
public interface TooltipBlock {

    void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipAdder, TooltipFlag pFlag);
}
