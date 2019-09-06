package al132.alchemistry.compat.jei;

import al132.alchemistry.blocks.combiner.CombinerContainer;
import al132.alchemistry.network.CombinerTransferPkt;
import al132.alchemistry.network.NetworkHandler;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class CombinerTransferHandler implements IRecipeTransferHandler<CombinerContainer> {
    @Override
    public Class<CombinerContainer> getContainerClass() {
        return CombinerContainer.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(CombinerContainer container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        int size = recipeLayout.getItemStacks().getGuiIngredients().size();
        //System.out.println("size: " + size);
        ItemStack output = recipeLayout.getItemStacks().getGuiIngredients().get(size - 1).getDisplayedIngredient();//last().value.displayedIngredient;

        if (output != null && doTransfer) {
            NetworkHandler.sendToServer(new CombinerTransferPkt(output, container.tile.getPos()));
        }
        return null;
    }
}
