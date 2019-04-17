package al132.alchemistry.tiles

import al132.alib.tiles.ALTile
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class TileBase : ALTile() {
    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block != newState.block;
    }
}
