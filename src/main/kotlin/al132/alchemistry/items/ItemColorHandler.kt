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
        else if (item is ItemElement && meta < ElementRegistry.size()) {
            return ElementRegistry[meta]!!.color.rgb
        } else if (item is ItemCompound && meta < CompoundRegistry.size()) {
            return CompoundRegistry.compounds[meta].color.rgb
        }

        return Color.BLACK.rgb
    }
}