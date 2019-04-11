package al132.alchemistry.blocks

import al132.alchemistry.items.ItemBlockFusionController
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyDirection
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

class FusionControllerBlock(name: String,
                            tileClass: Class<out TileEntity>,
                            guiID: Int)
    : BaseTileBlock(name, tileClass, guiID) {

    init {
        this.defaultState = this.blockState.baseState.withProperty(FACING, EnumFacing.NORTH)

    }
    override fun registerItemBlock(event: RegistryEvent.Register<Item>) {
        event.registry.register(ItemBlockFusionController(this).setRegistryName(this.registryName))
    }

    override fun createBlockState() = BlockStateContainer(this, *PROPERTIES)

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        val state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
        return state.withProperty(FACING, placer.horizontalFacing.opposite)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var enumfacing = EnumFacing.byIndex(meta)
        if (enumfacing.axis == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }
        return this.defaultState.withProperty(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).index
    }

    companion object {
        val FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)
        val PROPERTIES = arrayOf<IProperty<*>>(FACING)
    }
}