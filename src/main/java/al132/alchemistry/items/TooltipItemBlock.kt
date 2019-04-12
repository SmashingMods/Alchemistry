package al132.alchemistry.items

import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class TooltipItemBlock(block: Block, val tooltip: String) : ItemBlock(block){
    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltips: MutableList<String>?, flagIn: ITooltipFlag?) {
        (tooltips as MutableList).add(tooltip)
    }
}
