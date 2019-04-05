package al132.alchemistry.items

import al132.alchemistry.compat.jei.Translator
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World

/**
 * Created by al132 on 6/3/2018.
 */

class ItemBlockEvaporator(block: Block) : ItemBlock(block) {

    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        (tooltip as MutableList).add(Translator.translateToLocal("tile.evaporator.tooltip"))
    }
}