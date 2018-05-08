package al132.alchemistry.items

import al132.alchemistry.Reference
import al132.alib.items.ALItem
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModItems {

    var elements = ItemElement("element")
    var compounds = ItemCompound("compound")
    var mineralSalt = ItemBase("mineral_salt")
    var condensedMilk = ItemBase("condensed_milk")
    var diamondEnrichedGlass = ItemBase("diamond_enriched_glass")

    val items = arrayOf<ALItem>(
            elements,
            compounds,
            mineralSalt,
            condensedMilk,
            diamondEnrichedGlass
    )

    fun registerItems(event: RegistryEvent.Register<Item>) {
        items.forEach { it.registerItem(event) }
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() = items.forEach { it.registerModel() }


    @SideOnly(Side.CLIENT)
    fun initColors() {
        val colorHandler = ItemColorHandler()
        val itemColors = Minecraft.getMinecraft().itemColors

        itemColors.registerItemColorHandler(colorHandler, elements)
        itemColors.registerItemColorHandler(colorHandler, compounds)
    }
}


open class ItemBase(name: String) : ALItem(name, Reference.creativeTab)


abstract class ItemMetaBase(name: String) : ItemBase(name) {

    init {
        this.hasSubtypes = true
    }

    abstract override fun getUnlocalizedName(stack: ItemStack?): String
}
