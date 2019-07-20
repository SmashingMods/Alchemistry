package al132.alchemistry.network

import al132.alchemistry.SoundHandler
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class BoomPacket() : IMessage {
    var posList: MutableList<BlockPos> = ArrayList()

    override fun fromBytes(buf: ByteBuf?) {
        if (buf != null) {
            val size = buf.readInt()
            for (i in 0 until size) {
                val x = buf.readInt()
                val y = buf.readInt()
                val z = buf.readInt()
                posList.add(BlockPos(x, y, z))
            }
        }
    }

    override fun toBytes(buf: ByteBuf?) {
        if (buf != null) {
            buf.writeInt(posList.size)
            for (pos in posList) {
                buf.writeInt(pos.x)
                buf.writeInt(pos.y)
                buf.writeInt(pos.z)
            }
        }
    }

    constructor(posList: List<BlockPos>) : this() {
        this.posList = posList.toMutableList()
    }


    class Handler : IMessageHandler<BoomPacket, IMessage> {

        override fun onMessage(message: BoomPacket, ctx: MessageContext): IMessage? {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
            return null
        }

        private fun handle(message: BoomPacket, ctx: MessageContext) {
            val posList = message.posList
            posList.forEach {
                val x = it.x.toDouble()
                val y = it.y.toDouble()
                val z = it.z.toDouble()
                val world = Minecraft.getMinecraft().world
                world.playSound(x,y,z, SoundHandler.soaryn_boom, SoundCategory.BLOCKS,
                        0.75f, 1.0f, false)
                world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x, y, z, 0.0, 0.1, 0.0)
                world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0, 0.2, 0.0)
            }
        }
    }

}
