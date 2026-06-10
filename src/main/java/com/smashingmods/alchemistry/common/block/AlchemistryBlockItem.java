package com.smashingmods.alchemistry.common.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * The block-item used for every Alchemistry block. 1.21.5 removed the block-level {@code appendHoverText} hook, so
 * tooltip lines that used to live on the block move onto its item: when the backing block implements
 * {@link TooltipBlock} this forwards to it, letting each machine block keep authoring its own energy-requirement
 * line. Blocks that do not implement {@link TooltipBlock} behave exactly like a plain {@link BlockItem}.
 */
public class AlchemistryBlockItem extends BlockItem {

    public AlchemistryBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipAdder, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipAdder, pFlag);
        if (getBlock() instanceof TooltipBlock tooltipBlock) {
            tooltipBlock.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipAdder, pFlag);
        }
    }
}
