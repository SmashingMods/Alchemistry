package al132.alchemistry.compat.refinedstorage

import al132.alchemistry.items.ItemBase
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemChemPattern : ItemBase("chem_pattern"), ICraftingPatternProvider{

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }
    override fun create(world: World, stack: ItemStack, container: ICraftingPatternContainer?): ICraftingPattern {
        println(world)
        println(stack)
        println(container)
        return ChemPattern(world,container,stack)
    }
}