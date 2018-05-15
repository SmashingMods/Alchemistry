package al132.alchemistry

import al132.alchemistry.blocks.ModBlocks
import al132.alib.utils.extensions.toStack
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat

object Reference {

    const val MODID = "alchemistry"
    const val MODNAME = "Alchemistry"
    const val VERSION = "0.7.4"
    const val DEPENDENCIES = "required-after:forgelin;required-after:alib"
    val DECIMAL_FORMAT: NumberFormat = DecimalFormat("#0.00")

    val pathPrefix = "alchemistry:"
    lateinit var configPath: String
    lateinit var configDir: File

    val creativeTab: CreativeTabs = object : CreativeTabs("alchemistry") {
        override fun getTabIconItem(): ItemStack = ModBlocks.chemical_combiner.toStack()
    }
}