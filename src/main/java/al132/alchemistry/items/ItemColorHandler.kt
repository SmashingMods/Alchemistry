package al132.alchemistry.items

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

@SideOnly(Side.CLIENT)
class ItemColorHandler : IItemColor {

    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        val item = stack.item
        val meta = stack.metadata

        if (tintIndex != 0) return Color.WHITE.rgb
        else if (item is ItemElement && ElementRegistry.keys().contains(meta)) {
            return ElementRegistry[meta]!!.color.rgb
        } else if (item is ItemCompound && CompoundRegistry.keys().contains(meta)) {
            return CompoundRegistry[meta]!!.color.rgb
        } else return Color.BLACK.rgb
    }
}