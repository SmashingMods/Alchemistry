package al132.alchemistry.compat.jei;

import al132.alchemistry.blocks.combiner.CombinerContainer;
import al132.alchemistry.blocks.combiner.CombinerRecipe;
import al132.alchemistry.network.CombinerTransferPkt;
import al132.alchemistry.network.Messages;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CombinerTransferHandler implements IRecipeTransferHandler<CombinerContainer, CombinerRecipe> {
    @Override
    public Class<CombinerContainer> getContainerClass() {
        return CombinerContainer.class;
    }

    @Override
    public Class getRecipeClass() {
        return CombinerRecipe.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(CombinerContainer container, CombinerRecipe recipe, IRecipeLayout recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
        int size = recipeLayout.getItemStacks().getGuiIngredients().size();
        //System.out.println("size: " + size);
        ItemStack output = recipeLayout.getItemStacks().getGuiIngredients().get(size - 1).getDisplayedIngredient();//last().value.displayedIngredient;

        if (output != null && doTransfer) {
            Messages.sendToServer(new CombinerTransferPkt(output, container.tile.getBlockPos()));
        }
        return null;
    }
}
