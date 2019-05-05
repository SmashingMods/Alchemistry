package al132.alchemistry

import al132.alchemistry.capability.CapabilityDrugInfo
import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.items.ItemCompound
import al132.alib.utils.extensions.translate
import net.minecraft.item.ItemFood
import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ClientEventHandler {

    @SubscribeEvent
    fun fovEvent(e: FOVUpdateEvent) {
        val n : EnergyStorage
        e.entity.getCapability(CapabilityDrugInfo.DRUG_INFO, null)?.let { info ->
            if (info.psilocybinTicks > 500) {
                e.newfov = info.cumulativeFOVModifier// + e.fov
                info.cumulativeFOVModifier -= .002f
                info.psilocybinTicks--
            } else {
                info.cumulativeFOVModifier = 1.0f
            }
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