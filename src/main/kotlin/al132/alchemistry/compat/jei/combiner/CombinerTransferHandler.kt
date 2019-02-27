package al132.alchemistry.compat.jei.combiner

import al132.alchemistry.client.ContainerChemicalCombiner
import al132.alchemistry.network.ChemicalCombinerTransferPacket
import al132.alchemistry.network.PacketHandler
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class CombinerTransferHandler : IRecipeTransferHandler<ContainerChemicalCombiner> {
    override fun getContainerClass() = ContainerChemicalCombiner::class.java

    override fun transferRecipe(container: ContainerChemicalCombiner, recipeLayout: IRecipeLayout, player: EntityPlayer, maxTransfer: Boolean, doTransfer: Boolean): IRecipeTransferError? {
        val output: ItemStack? = recipeLayout.itemStacks.guiIngredients.entries.last().value.displayedIngredient
        if (output != null) {
            PacketHandler.INSTANCE!!.sendToServer(ChemicalCombinerTransferPacket(container.tile.pos, output))
        }
        return null
    }
}