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
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * The block-item used for every Alchemistry block. On 1.21.3 {@link BlockItem#appendHoverText} still delegates to the
 * backing block's own {@code appendHoverText} -- the machine description and energy-requirement lines -- so this only
 * appends the capitalized mod name afterwards, mirroring ChemLib's blue tooltip footer so both mods read consistently.
 */
public class AlchemistryBlockItem extends BlockItem {

    public AlchemistryBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
        pTooltip.add(MutableComponent.create(
                PlainTextContents.create(StringUtils.capitalize(getNamespace()))).withStyle(Alchemistry.MOD_ID_TEXT_STYLE));
    }

    private String getNamespace() {
        return BuiltInRegistries.ITEM.getResourceKey(this).get().location().getNamespace();
    }
}
