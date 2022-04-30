package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CombinerTransferHandler implements IRecipeTransferHandler<CombinerMenu, CombinerRecipe>, IRecipeTransferInfo<CombinerMenu, CombinerRecipe> {

    @Override
    public Class<CombinerMenu> getContainerClass() {
        return CombinerMenu.class;
    }

    @Override
    public boolean canHandle(CombinerMenu combinerMenu, CombinerRecipe combinerRecipe) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(CombinerMenu combinerMenu, CombinerRecipe combinerRecipe) {
        return null;
    }

    @Override
    public List<Slot> getInventorySlots(CombinerMenu combinerMenu, CombinerRecipe combinerRecipe) {
        return null;
    }

    @Override
    public Class<CombinerRecipe> getRecipeClass() {
        return CombinerRecipe.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return null;
    }


    @Override
    public @org.jetbrains.annotations.Nullable IRecipeTransferError transferRecipe(CombinerMenu pMenu, CombinerRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {



        return IRecipeTransferHandler.super.transferRecipe(pMenu, pRecipe, pRecipeSlots, pPlayer, pMaxTransfer, pDoTransfer);
    }
}
