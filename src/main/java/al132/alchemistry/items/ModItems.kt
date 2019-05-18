package al132.alchemistry.items

import al132.alchemistry.Reference
import al132.alib.items.ALItem
import al132.alib.utils.extensions.translate
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModItems {

    val items = ArrayList<ALItem>()

    var MILK = object : ItemBase("milk") {
        override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
            tooltip.add("item.alchemistry:milk.tooltip".translate())
        }
    }
    var mineralSalt = ItemBase("mineral_salt")
    var condensedMilk = ItemBase("condensed_milk")
    var fertilizer = ItemFertilizer()
    var obsidianBreaker = object : ItemBase("obsidian_breaker") {
        override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
            tooltip.add("item.alchemistry:obsidian_breaker.tooltip".translate())
        }
    }
    var slotFiller = ItemSlotFiller()

    var elements = ItemElement("element")
    var compounds = ItemCompound("compound")
    var ingots = ItemElementIngot("ingot")


    fun registerItems(event: RegistryEvent.Register<Item>) = items.forEach { it.registerItem(event) }

    @SideOnly(Side.CLIENT)
    fun registerModels() = items.forEach { it.registerModel() }

    @SideOnly(Side.CLIENT)
    fun initColors() {
        val colorHandler = ItemColorHandler()
        val itemColors = Minecraft.getMinecraft().itemColors
        itemColors.registerItemColorHandler(colorHandler, compounds)
        itemColors.registerItemColorHandler(colorHandler, ingots)
    }
}

open class ItemBase(name: String) : ALItem(name, Reference.creativeTab) {
    init {
        ModItems.items.add(this)
    }
}

abstract class ItemMetaBase(name: String) : ItemBase(name) {

    init {
        this.hasSubtypes = true
    }
}