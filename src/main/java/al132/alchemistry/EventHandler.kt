package al132.alchemistry

import al132.alchemistry.ClientProxy.Companion.cumulativeFovModifier
import al132.alchemistry.items.ModItems
import al132.alib.utils.extensions.toStack
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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


}