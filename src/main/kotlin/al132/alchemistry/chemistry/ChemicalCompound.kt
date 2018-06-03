package al132.alchemistry.chemistry

import al132.alchemistry.items.ModItems
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.awt.Color

/**
 * Created by al132 on 1/22/2017.
 */

inline fun Compound(crossinline init: ChemicalCompound.() -> Unit) = ChemicalCompound().apply { init() }


fun find(name: String): ICompoundComponent? {
    ElementRegistry[name]?.let { return it }
    CompoundRegistry[name]?.let { return it }
    return null
}


data class CompoundPair(val compound: ICompoundComponent, val quantity: Int) {

    constructor(name: String, quantity: Int) : this(find(name)!!, { quantity }())

    fun toStack() = compound.toItemStack(quantity)
}


class ChemicalCompound constructor(override var name: String = "",
                                   override var color: Color = Color.WHITE,
                                   var autoCombinerRecipe: Boolean = true,
                                   var autoDissolverRecipe: Boolean = true,
                                   var components: List<CompoundPair> = ArrayList<CompoundPair>()) : ICompoundComponent {

    override val item: Item
        get() = ModItems.compounds

    override fun toItemStack(quantity: Int) = ItemStack(item, quantity, this.meta)

    fun toItemStackList(): List<ItemStack> = components.mapTo(ArrayList<ItemStack>(), { it.toStack() })

    override val meta: Int
        get() = CompoundRegistry.getMeta(this.name)

    override fun toAbbreviatedString(): String {
        val builder = StringBuilder()
        for (component in components) {
            if (component.compound is ChemicalCompound) {
                builder.append("(" + component.compound.toAbbreviatedString() + ")")
            } else {
                builder.append(component.compound.toAbbreviatedString())
            }
            if (component.quantity > 1) {
                //val subscriptZeroCodepoint is subscript 0 unicode char, adding 1-9 gives the subscript for that num
                //i.e. ₀ + 3 = ₃
                val subscriptZeroCodepoint: Int = Character.codePointAt("₀", 0)
                component.quantity.toString().chars().forEach {
                    builder.append(Character.toChars(subscriptZeroCodepoint + Character.getNumericValue(it)))
                }
            }
        }
        return builder.toString()
    }

}