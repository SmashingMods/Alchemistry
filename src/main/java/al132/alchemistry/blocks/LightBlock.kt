package al132.alchemistry.blocks

import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer

class LightBlock(name: String) : BaseBlock(name){
    init {
        this.setLightLevel(1.0f)
        this.soundType = SoundType.GLASS
    }

    override fun isOpaqueCube(state: IBlockState) = false

    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT
}