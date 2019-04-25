package al132.alchemistry

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.items.ItemCompound
import al132.alib.utils.extensions.translate
import net.minecraft.item.ItemFood
import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
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

    @SubscribeEvent
    fun tooltipEvent(e: ItemTooltipEvent) {
        val stack = e.itemStack
        if (stack.item is ItemFood && stack.hasTagCompound()
                && stack.tagCompound!!.hasKey("alchemistryPotion")
                && !stack.tagCompound!!.getBoolean("alchemistrySalted")) {
            val molecule = ItemCompound.getDankMoleculeForMeta(stack.tagCompound!!.getInteger("alchemistryPotion"))
            if (molecule != null) {
                val compoundName = CompoundRegistry[molecule.meta]?.toItemStack(1)?.displayName
                        ?: "<Invalid Compound>"
                e.toolTip.add("§b" + "spiked_food.tooltip".translate() + " " + compoundName + "§r")
            }

        }
    }
}