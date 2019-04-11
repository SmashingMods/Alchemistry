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

    val blocks = ArrayList<ALBlock>()

    var electrolyzer = ElectrolyzerBlock("electrolyzer", TileElectrolyzer::class.java, GuiHandler.ELECTROLYZER_ID)
    var chemical_dissolver = ChemicalDissolverBlock("chemical_dissolver", TileChemicalDissolver::class.java, GuiHandler.CHEMICAL_DISSOLVER_ID)
    var chemical_combiner = ChemicalCombinerBlock("chemical_combiner", TileChemicalCombiner::class.java, GuiHandler.CHEMICAL_COMBINER_ID)
    var evaporator = EvaporatorBlock("evaporator", TileEvaporator::class.java, GuiHandler.EVAPORATOR_ID)
    val atomizer = AtomizerBlock("atomizer", TileAtomizer::class.java, GuiHandler.ATOMIZER_ID)
    val liquifier = LiquifierBlock("liquifier", TileLiquifier::class.java, GuiHandler.LIQUIFIER_ID)

    val fissionCasing: BaseBlock = BaseBlock("fission_casing")
    val fissionCore = BaseBlock("fission_core")
    val fissionController = FissionControllerBlock("fission_controller", TileFissionController::class.java, GuiHandler.FISSION_CONTROLLER_ID)

    val fusionCasing = BaseBlock("fusion_casing")
    val fusionCore = BaseBlock("fusion_core")
    val fusionController = FusionControllerBlock("fusion_controller",TileFusionController::class.java,GuiHandler.FUSION_CONTROLLER_ID)


    fun registerBlocks(event: RegistryEvent.Register<Block>) = blocks.forEach { it.registerBlock(event) }

    fun registerItemBlocks(event: RegistryEvent.Register<Item>) = blocks.forEach { it.registerItemBlock(event) }

    @SideOnly(Side.CLIENT)
    fun registerModels() = blocks.forEach { it.registerModel() }
}

open class BaseBlock(name: String) : ALBlock(name, Reference.creativeTab) {
    init {
        ModBlocks.blocks.add(this)
    }
}

open class BaseTileBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int)
    : ALTileBlock(name, Reference.creativeTab, tileClass, Alchemistry, guiID) {
    init {
        ModBlocks.blocks.add(this)
    }
}