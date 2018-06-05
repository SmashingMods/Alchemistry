package al132.alchemistry.network


import al132.alchemistry.tiles.TileChemicalCombiner
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ChemicalCombinerPacket() : IMessage {


    private var blockPos: BlockPos? = null
    private var lock = false
    private var pause = false

    override fun fromBytes(buf: ByteBuf) {
        this.blockPos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        this.lock = buf.readBoolean()
        this.pause = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(blockPos!!.x)
        buf.writeInt(blockPos!!.y)
        buf.writeInt(blockPos!!.z)
        buf.writeBoolean(this.lock)
        buf.writeBoolean(this.pause)
    }

    constructor(pos: BlockPos, lock: Boolean = false, pause: Boolean = false) : this() {
        this.blockPos = pos
        this.lock = lock
        this.pause = pause
    }

    class Handler : IMessageHandler<ChemicalCombinerPacket, IMessage> {

        override fun onMessage(message: ChemicalCombinerPacket, ctx: MessageContext): IMessage? {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
            return null
        }

        private fun handle(message: ChemicalCombinerPacket, ctx: MessageContext) {
            val playerEntity = ctx.serverHandler.player

            val tile = playerEntity.world.getTileEntity(message.blockPos!!)
            if (tile is TileChemicalCombiner) {
                if(message.lock) {
                    tile.recipeIsLocked = !(tile.recipeIsLocked)
                    tile.markDirtyClient()
                }
                else if(message.pause){
                    tile.paused = !(tile.paused)
                    tile.markDirtyClient()
                }
            }
        }
    }
}