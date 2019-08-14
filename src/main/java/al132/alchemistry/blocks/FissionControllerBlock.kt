package al132.alchemistry.blocks

import al132.alchemistry.ConfigHandler
import al132.alchemistry.items.TooltipItemBlock
import al132.alib.utils.Translator
import al132.alib.utils.extensions.translate
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent

class FissionControllerBlock(name: String,
                             tileClass: Class<out TileEntity>,
                             guiID: Int)
    : BaseTileBlock(name, tileClass, guiID) {

    init {
        this.defaultState = this.blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
                .withProperty(STATUS, PropertyPowerStatus.OFF)

    }

    override fun registerItemBlock(event: RegistryEvent.Register<Item>) {
        event.registry.register(TooltipItemBlock(this,
                Translator.translateToLocalFormatted("tooltip.alchemistry.energy_requirement",
                        ConfigHandler.fissionEnergyPerTick ?: "?"))
                .setRegistryName(this.registryName))
    }

    override fun createBlockState() = BlockStateContainer(this, *PROPERTIES)

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        val state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
        return state.withProperty(FACING, placer.horizontalFacing.opposite).withProperty(STATUS, PropertyPowerStatus.OFF)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        lateinit var facing: EnumFacing
        lateinit var status: PropertyPowerStatus
        when (meta) {
            in 0 until 3  -> facing = EnumFacing.NORTH
            in 3 until 6  -> facing = EnumFacing.SOUTH
            in 6 until 9  -> facing = EnumFacing.WEST
            in 9 until 12 -> facing = EnumFacing.EAST
            else          -> facing = EnumFacing.NORTH
        }
        when (meta % 3) {
            0 -> status = PropertyPowerStatus.OFF
            1 -> status = PropertyPowerStatus.STANDBY
            2 -> status = PropertyPowerStatus.ON
        }
        /* var enumfacing = EnumFacing.byIndex(meta)
         if (enumfacing.axis == EnumFacing.Axis.Y) {
             enumfacing = EnumFacing.NORTH
         }
         return this.defaultState.withProperty(FACING, enumfacing)
         */
        return this.defaultState.withProperty(FACING, facing).withProperty(STATUS, status)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        //val dir: Int = (state.getValue(FACING) as EnumFacing).index
        var sum = 0
        when (state.getValue(FACING)) {
            EnumFacing.NORTH -> sum += 0
            EnumFacing.SOUTH -> sum += 3
            EnumFacing.WEST  -> sum += 6
            EnumFacing.EAST  -> sum += 9
        }
        when (state.getValue(STATUS)) {
            PropertyPowerStatus.OFF     -> sum += 0
            PropertyPowerStatus.STANDBY -> sum += 1
            PropertyPowerStatus.ON      -> sum += 2
            else                        -> sum += 0
        }
        return sum
    }

    companion object {
        val FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)
        val STATUS = PropertyEnum.create("status", PropertyPowerStatus::class.java)
        val PROPERTIES = arrayOf<IProperty<*>>(FACING, STATUS)
    }
}