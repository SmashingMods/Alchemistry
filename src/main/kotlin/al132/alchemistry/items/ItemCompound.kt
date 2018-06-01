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
        (0 until CompoundRegistry.size()).forEach {
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
    override fun getSubItems(itemIn: CreativeTabs, tab: NonNullList<ItemStack>) {
        if (itemIn == Reference.creativeTab) {
            (0 until CompoundRegistry.size()).forEach { tab.add(ItemStack(this, 1, it)) }
        }
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        var i = stack!!.itemDamage
        if (!(0 until CompoundRegistry.size()).contains(i)) i = 0
        //if (i < 0 || i >= CompoundRegistry.size()) i = 0
        return super.getUnlocalizedName() + "_" + CompoundRegistry.compounds[i].name
    }
}