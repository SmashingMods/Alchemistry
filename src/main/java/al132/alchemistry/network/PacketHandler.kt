package al132.alchemistry.network

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object PacketHandler {
    private var packetId = 0
    var INSTANCE: SimpleNetworkWrapper? = null

    fun nextID(): Int = packetId++

    fun registerMessages(channelName: String) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName)
        registerMessages()
    }

    fun registerMessages() {
        INSTANCE!!.registerMessage(
                ChemicalCombinerPacket.Handler::class.java, ChemicalCombinerPacket::class.java, nextID(), Side.SERVER)
        INSTANCE!!.registerMessage(
                ChemicalCombinerTransferPacket.Handler::class.java, ChemicalCombinerTransferPacket::class.java, nextID(), Side.SERVER)
        INSTANCE!!.registerMessage(
                BoomPacket.Handler::class.java,BoomPacket::class.java,nextID(),Side.CLIENT)
    }
}