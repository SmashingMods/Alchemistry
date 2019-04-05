package al132.alchemistry.blocks

import al132.alchemistry.client.TESREvaporator
import al132.alchemistry.items.ItemBlockEvaporator
import al132.alchemistry.tiles.TileEvaporator
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry

/**
 * Created by al132 on 6/21/2017.
 */

class EvaporatorBlock(name: String,
                      tileClass: Class<out TileEntity>,
                      guiID: Int)
    : BaseTileBlock(name, tileClass, guiID) {

    val boundingBox = AxisAlignedBB(0.0625, 0.0625, 0.0625, 0.9375, 0.75, 0.9375)
    val boundingBox2 = AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.0625, 0.75)

    override fun registerModel() {
        super.registerModel()
        ClientRegistry.bindTileEntitySpecialRenderer(TileEvaporator::class.java, TESREvaporator())
    }

    override fun registerItemBlock(event: RegistryEvent.Register<Item>){
        event.registry.register(ItemBlockEvaporator(ModBlocks.evaporator).setRegistryName(this.registryName))
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.MODEL

    override fun isOpaqueCube(state: IBlockState) = false

    override fun isFullCube(state: IBlockState?): Boolean = false

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = boundingBox

    override fun addCollisionBoxToList(state: IBlockState,
                                       worldIn: World,
                                       pos: BlockPos,
                                       entityBox: AxisAlignedBB,
                                       collidingBoxes: List<AxisAlignedBB>,
                                       entityIn: Entity?, mysteryboolean: Boolean) {

        addCollisionBoxToList(pos, entityBox, collidingBoxes, boundingBox)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boundingBox2)
    }
}