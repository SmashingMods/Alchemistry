package al132.alchemistry.items

import al132.alchemistry.chemistry.ElementRegistry
import al132.alib.utils.extensions.toStack
import al132.alib.utils.extensions.translate
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemElementIngot(name: String) : ItemMetaBase(name) {
    @SideOnly(Side.CLIENT)
    override fun registerModel() {
        ElementRegistry.keys()
                .filter { it <= 118 && !invalidIngots.contains(it) }
                .forEach {
                    ModelLoader.setCustomModelResourceLocation(this, it,
                            ModelResourceLocation(registryName.toString(), "inventory"))
                }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!isInCreativeTab(tab)) return;
        ElementRegistry.keys()
                .filter { it <= 118 && !invalidIngots.contains(it) }
                .forEach { items.add(ItemStack(this, 1, it)) }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        var i = stack.metadata
        if (!ElementRegistry.keys().contains(i)) i = 1
        val elementName = ModItems.elements.toStack(meta = i)
        //I assume that other languages use Iron ingot rather than ingot iron, i probably shouldn't
        return (elementName.translationKey + ".name").translate() + " " + "element_ingot.name".translate()
    }

    companion object {
        val invalidIngots = listOf(1, 2, 6, 7, 8, 9, 10, 15, 16, 17, 18, 26, 35, 36, 53, 54, 79, 80, 86)
    }
}