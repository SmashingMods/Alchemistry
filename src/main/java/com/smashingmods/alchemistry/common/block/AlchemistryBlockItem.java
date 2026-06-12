package com.smashingmods.alchemistry.common.block;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * The block-item used for every Alchemistry block. 1.21.5 removed the block-level {@code appendHoverText} hook, so
 * tooltip lines that used to live on the block move onto its item: when the backing block implements
 * {@link TooltipBlock} this forwards to it, letting each machine block keep authoring its own energy-requirement
 * line. After the forwarded lines it appends the capitalized mod name, mirroring ChemLib's blue tooltip footer so
 * both mods read consistently.
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
        pTooltipAdder.accept(MutableComponent.create(
                PlainTextContents.create(StringUtils.capitalize(getNamespace()))).withStyle(Alchemistry.MOD_ID_TEXT_STYLE));
    }

    private String getNamespace() {
        return BuiltInRegistries.ITEM.getResourceKey(this).get().location().getNamespace();
    }
}
