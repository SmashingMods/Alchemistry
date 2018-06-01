package al132.alchemistry.chemistry

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.awt.Color

/**
 * Created by al132 on 1/22/2017.
 */
interface ICompoundComponent {

    var color: Color
    var name: String
    val item: Item
    val meta: Int
    fun toItemStack(quantity: Int): ItemStack
    fun toAbbreviatedString(): String
}