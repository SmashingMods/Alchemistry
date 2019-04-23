package al132.alchemistry

import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ClientEventHandler {

    @SubscribeEvent
    fun fovEvent(e: FOVUpdateEvent) {
        if (ClientProxy.highAF > 500) {
            e.newfov = ClientProxy.cumulativeFovModifier// + e.fov
            ClientProxy.cumulativeFovModifier -= .002f
            ClientProxy.highAF--
        } else {
            ClientProxy.cumulativeFovModifier = 1.0f
        }
    }
}