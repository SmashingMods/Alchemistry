package al132.alchemistry

import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries


object SoundHandler {
    lateinit var soaryn_boom: SoundEvent

    fun init() {
        soaryn_boom = register("block.soaryn_boom")
    }

    fun register(name: String): SoundEvent {
        val location = ResourceLocation(Reference.MODID, name)
        val event = SoundEvent(location)
        ForgeRegistries.SOUND_EVENTS.register(event.setRegistryName(location))
        return event
    }
}