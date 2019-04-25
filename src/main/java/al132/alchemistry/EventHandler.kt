package al132.alchemistry

import al132.alchemistry.items.DankMolecule
import al132.alchemistry.items.ItemCompound
import al132.alchemistry.items.ModItems
import al132.alib.utils.extensions.toStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

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
}