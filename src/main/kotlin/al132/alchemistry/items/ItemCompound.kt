package al132.alchemistry.items

import al132.alchemistry.Reference
import al132.alchemistry.chemistry.ChemicalCompound
import al132.alchemistry.chemistry.CompoundRegistry
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by al132 on 1/16/2017.
 */
class ItemCompound(name: String) : ItemMetaBase(name) {

    @SideOnly(Side.CLIENT)
    override fun registerModel() {
        CompoundRegistry.keys().forEach {
            ModelLoader.setCustomModelResourceLocation(this, it,
                    ModelResourceLocation(registryName.toString(), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, playerIn: World?, tooltip: List<String>, advanced: ITooltipFlag) {
        val compound: ChemicalCompound? = CompoundRegistry[stack.itemDamage]
        compound?.let { (tooltip as MutableList).add(compound.toAbbreviatedString()) }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, stacks: NonNullList<ItemStack>) {
        if (tab == Reference.creativeTab) {
            CompoundRegistry.keys().forEach { stacks.add(ItemStack(this, 1, it)) }
        }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val compound = CompoundRegistry[stack.metadata]
        if (stack.item == ModItems.compounds && compound != null && !(compound.isInternalCompound)) {
            val compoundName = CompoundRegistry[stack.metadata]?.name ?: "<Error>"
            return compoundName.split("_").joinToString(separator = " ") { it.first().toUpperCase() + it.drop(1) }
        } else return super.getItemStackDisplayName(stack)
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        var i = stack!!.itemDamage
        if (!CompoundRegistry.keys().contains(i)) i = 0
        return super.getUnlocalizedName() + "_" + CompoundRegistry[i]!!.name
    }
}