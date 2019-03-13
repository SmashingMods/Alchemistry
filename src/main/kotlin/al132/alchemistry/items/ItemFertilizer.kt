package al132.alchemistry.items

import al132.alchemistry.compat.jei.Translator
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemDye.applyBonemeal
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by al132 on 6/26/2018.
 */
class ItemFertilizer : ItemBase("Fertilizer") {

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val pos = pos
        val itemstack = player.getHeldItem(hand)

        if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) return EnumActionResult.FAIL
        else {
            if (applyBonemeal(itemstack, worldIn, pos, player, hand)) {
                if (!worldIn.isRemote) {
                    worldIn.playEvent(2005, pos, 0)
                }
                return EnumActionResult.SUCCESS
            }
            return EnumActionResult.PASS
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, playerIn: World?, tooltip: List<String>, advanced: ITooltipFlag) {
        (tooltip as MutableList).add(Translator.translateToLocal("item.alchemistry:fertilizer.tooltip"))
    }
}
