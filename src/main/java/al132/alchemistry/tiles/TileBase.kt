package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alib.Reference
import al132.alib.tiles.ALTile
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

abstract class TileBase : ALTile() {
    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block != newState.block;
    }

    fun markDirtyGUI(){
        markDirty()
        world?.let {
            val state = world.getBlockState(getPos())
            world.notifyBlockUpdate(pos, state, state, 6)
        }
    }

    fun markDirtyGUIEvery(ticks : Int){
        this.dirtyTicks++
        if (this.dirtyTicks >= ticks) {
            this.markDirtyGUI()
            this.dirtyTicks = 0
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (Reference.ITEM_CAP == capability && (!ConfigHandler.enableAutomation!!)) {
            return false;
        } else return super.hasCapability(capability, facing)
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (Reference.ITEM_CAP == capability && (!ConfigHandler.enableAutomation!!)) {
            return null
        } else return super.getCapability(capability, facing)
    }
}
