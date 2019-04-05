package al132.alchemistry.chemistry

import al132.alchemistry.items.ModItems
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.awt.Color

/**
 * Created by al132 on 1/22/2017.
 */
class ChemicalElement constructor(override var name: String, val abbreviation: String, override var color: Color = Color.white) : ICompoundComponent {

    override val item: Item
        get() = ModItems.elements

    override val meta: Int
        get() = ElementRegistry.getMeta(this.name)

    override fun toItemStack(quantity: Int) = ItemStack(item, quantity, this.meta)

    override fun toString(): String = "Element: $name"

    override fun toAbbreviatedString(): String = this.abbreviation
}