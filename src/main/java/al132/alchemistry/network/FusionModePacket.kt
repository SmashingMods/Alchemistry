package al132.alchemistry.network


import al132.alchemistry.tiles.TileFusionController
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class FusionModePacket() : IMessage {

    private var blockPos: BlockPos? = null
    private var singleMode = false

    override fun fromBytes(buf: ByteBuf) {
        this.blockPos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        this.singleMode = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(blockPos!!.x)
        buf.writeInt(blockPos!!.y)
        buf.writeInt(blockPos!!.z)
        buf.writeBoolean(this.singleMode)
    }

    constructor(pos: BlockPos, singleMode: Boolean = false) : this() {
        this.blockPos = pos
        this.singleMode = singleMode
    }

    class Handler : IMessageHandler<FusionModePacket, IMessage> {

        override fun onMessage(message: FusionModePacket, ctx: MessageContext): IMessage? {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
            return null
        }

        private fun handle(message: FusionModePacket, ctx: MessageContext) {
            val playerEntity = ctx.serverHandler.player

            val tile = playerEntity.world.getTileEntity(message.blockPos!!)
            if (tile is TileFusionController) {
                 if (message.singleMode) {
                    tile.singleMode = !(tile.singleMode)
                }
            }
        }
    }
}