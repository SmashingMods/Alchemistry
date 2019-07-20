package al132.alchemistry

import al132.alchemistry.capability.AlchemistryDrugDispatcher
import al132.alchemistry.capability.CapabilityDrugInfo
import al132.alchemistry.items.DankMolecule
import al132.alchemistry.items.ItemCompound
import al132.alchemistry.items.ModItems
import al132.alchemistry.network.BoomPacket
import al132.alchemistry.network.PacketHandler
import al132.alib.utils.extensions.toStack
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class EventHandler {

    @SubscribeEvent
    fun rightClickEvent(e: PlayerInteractEvent.RightClickBlock) {
        val target = e.world.getBlockState(e.pos)
        if (e.itemStack.item == ModItems.obsidianBreaker && target.block == Blocks.OBSIDIAN) {
            e.itemStack.shrink(1)
            e.world.setBlockToAir(e.pos)
            e.entityPlayer.addItemStackToInventory(Blocks.OBSIDIAN.toStack())
        }
    }

    @SubscribeEvent
    fun finishItemEvent(e: LivingEntityUseItemEvent.Finish) {
        if (e.item.hasTagCompound()) {
            val tag = e.item.tagCompound
            if (tag?.hasKey("alchemistryPotion") ?: false) {
                val moleculeMeta = tag!!.getInteger("alchemistryPotion")
                val molecule: DankMolecule? = ItemCompound.getDankMoleculeForMeta(moleculeMeta)
                if (molecule != null && e.entityLiving is EntityPlayer) molecule.activateForPlayer(e.entityLiving as EntityPlayer)
            }
        }
    }

    @SubscribeEvent
    fun onEntityConstructing(event: AttachCapabilitiesEvent<Entity>) {
        if (event.getObject() is EntityPlayer) {
            if (!event.getObject().hasCapability(CapabilityDrugInfo.DRUG_INFO, null)) {
                event.addCapability(ResourceLocation(Reference.MODID, "DrugInfo"), AlchemistryDrugDispatcher())
            }
        }
    }

    @SubscribeEvent
    fun fluidBooming(e: TickEvent.WorldTickEvent) {
        val worldElements = e.world.loadedEntityList
                .filter {
                    it is EntityItem
                            && (it.item.item == ModItems.elements || it.item.item == ModItems.ingots)
                            && it.isInWater
                            && listOf(3, 11, 19, 37, 55, 87).contains(it.item.metadata)
                } as List<EntityItem>

        worldElements.forEach {
            if (it.item.item == ModItems.ingots) {
                e.world.createExplosion(null, it.posX, it.posY + 1, it.posZ, 2.0f, true)
            } else {
                PacketHandler.INSTANCE!!.sendToDimension(BoomPacket(listOf(it.position)), it.dimension)
            }
            it.setDead()
        }
    }
}