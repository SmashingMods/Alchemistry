package al132.alchemistry.blocks

import al132.alchemistry.items.TooltipItemBlock
import al132.alib.utils.extensions.translate
import net.minecraft.block.Block
import net.minecraft.block.BlockFalling.fallInstantly
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.init.Blocks
import net.minecraft.init.Blocks.CACTUS
import net.minecraft.init.Blocks.REEDS
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

class WetSandBlock : BaseBlock("wet_sand", Material.SAND) {
    init {
        this.setHardness(.5f)
        this.setResistance(1.0f)
    }

    override fun registerItemBlock(event: RegistryEvent.Register<Item>) {
        event.registry.register(TooltipItemBlock(this,
                "tile.wet_sand.tooltip".translate()).setRegistryName(this.registryName))
    }

    override fun canSustainPlant(state: IBlockState, world: IBlockAccess, pos: BlockPos, direction: EnumFacing, plantable: IPlantable): Boolean {
        val plant = plantable.getPlant(world, pos.offset(direction))
        return plant.block === CACTUS || plant.block === REEDS
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            this.checkFallable(worldIn, pos)
        }
    }

    private fun checkFallable(worldIn: World, pos: BlockPos) {
        if ((worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down()))) && pos.y >= 0) {
            val i = 32

            if (!fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                if (!worldIn.isRemote) {
                    val entityfallingblock = EntityFallingBlock(worldIn, pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5, worldIn.getBlockState(pos))
                    this.onStartFalling(entityfallingblock)
                    worldIn.spawnEntity(entityfallingblock)
                }
            } else {
                val state = worldIn.getBlockState(pos)
                worldIn.setBlockToAir(pos)
                var blockpos: BlockPos

                blockpos = pos.down()
                while ((worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.y > 0) {
                    blockpos = blockpos.down()
                }

                if (blockpos.y > 0) {
                    worldIn.setBlockState(blockpos.up(), state) //Forge: Fix loss of state information during world gen.
                }
            }
        }
    }

    protected fun onStartFalling(fallingEntity: EntityFallingBlock) {}

    /**
     * How many world ticks before ticking
     */
    override fun tickRate(worldIn: World): Int = 2

    fun canFallThrough(state: IBlockState): Boolean {
        val block = state.block
        val material = state.material
        return block === Blocks.FIRE || material === Material.AIR || material === Material.WATER || material === Material.LAVA
    }

    fun onEndFalling(worldIn: World, pos: BlockPos, p_176502_3_: IBlockState, p_176502_4_: IBlockState) {}

    fun onBroken(worldIn: World, pos: BlockPos) {}

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        if (rand.nextInt(16) == 0) {
            val blockpos = pos.down()

            if (canFallThrough(worldIn.getBlockState(blockpos))) {
                val d0 = (pos.x.toFloat() + rand.nextFloat()).toDouble()
                val d1 = pos.y.toDouble() - 0.05
                val d2 = (pos.z.toFloat() + rand.nextFloat()).toDouble()
                worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0, 0.0, 0.0, Block.getStateId(stateIn))
            }
        }
    }

    @SideOnly(Side.CLIENT)
    fun getDustColor(state: IBlockState): Int {
        return -16777216
    }
}