package al132.alchemistry.blocks

import al132.alchemistry.Alchemistry
import al132.alchemistry.Reference
import al132.alchemistry.client.GuiHandler
import al132.alchemistry.tiles.*
import al132.alib.blocks.ALBlock
import al132.alib.blocks.ALTileBlock
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModBlocks {

    var electrolyzer = ElectrolyzerBlock("electrolyzer", TileElectrolyzer::class.java, GuiHandler.ELECTROLYZER_ID)
    var chemical_dissolver = BaseTileBlock("chemical_dissolver", TileChemicalDissolver::class.java, GuiHandler.CHEMICAL_DISSOLVER_ID)
    var chemical_combiner = ChemicalCombinerBlock("chemical_combiner", TileChemicalCombiner::class.java, GuiHandler.CHEMICAL_COMBINER_ID)
    var evaporator = EvaporatorBlock("evaporator", TileEvaporator::class.java, GuiHandler.EVAPORATOR_ID)
    val atomizer = BaseTileBlock("atomizer", TileAtomizer::class.java,GuiHandler.ATOMIZER_ID)
    //var alloy_furnace = BaseTileBlock("alloy_furnace", TileAlloyFurnace::class.java, GuiHandler.ALLOY_FURNACE_ID )

    val blocks = arrayOf<ALBlock>(
            electrolyzer,
            chemical_dissolver,
            chemical_combiner,
            evaporator,
            atomizer
          //  alloy_furnace
    )

    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        blocks.forEach { it.registerBlock(event) }
    }

    fun registerItemBlocks(event: RegistryEvent.Register<Item>) {
        blocks.forEach { it.registerItemBlock(event) }
    }

    @SideOnly(Side.CLIENT)
    fun registerModels() = blocks.forEach { it.registerModel() }
}


open class BaseBlock(name: String) : ALBlock(name, Reference.creativeTab)

open class BaseTileBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int)
    : ALTileBlock(name, Reference.creativeTab, tileClass, Alchemistry, guiID)