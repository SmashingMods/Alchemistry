package al132.alchemistry.items

import al132.alchemistry.chemistry.ChemicalElement
import al132.alchemistry.chemistry.ElementRegistry
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
class ItemElement(name: String) : ItemMetaBase(name) {

    @SideOnly(Side.CLIENT)
    override fun registerModel() {
        ElementRegistry.keys().forEach {
            ModelLoader.setCustomModelResourceLocation(this, it,
                    ModelResourceLocation(registryName.toString(), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, playerIn: World?, tooltip: List<String>, advanced: ITooltipFlag) {
        val element: ChemicalElement? = ElementRegistry[stack.itemDamage]
        element?.let {
            (tooltip as MutableList).add(element.abbreviation + " - " + element.meta)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!isInCreativeTab(tab)) return;
        ElementRegistry.keys().forEach { items.add(ItemStack(this, 1, it)) }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val element = ElementRegistry[stack.metadata]
        if (stack.metadata > 118 && stack.item == ModItems.elements) {
            val elementName = ElementRegistry[stack.metadata]?.name ?: "<Error>"
            return elementName.split("_").joinToString(separator = " ") { it.first().toUpperCase() + it.drop(1) }
        } else return super.getItemStackDisplayName(stack)
    }

    override fun getTranslationKey(stack: ItemStack?): String {
        var i = stack!!.itemDamage
        if (!ElementRegistry.keys().contains(i)) i = 1
        return super.getTranslationKey() + "_" + ElementRegistry[i]!!.name.toLowerCase()
    }
}