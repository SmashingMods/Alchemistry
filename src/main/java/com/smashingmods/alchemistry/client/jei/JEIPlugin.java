package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.category.*;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerScreen;
import com.smashingmods.alchemistry.common.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.common.block.compactor.CompactorScreen;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverScreen;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerScreen;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerScreen;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierScreen;
import com.smashingmods.alchemistry.common.network.jei.*;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Objects;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Alchemistry.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration pRegistration) {
        IGuiHelper guiHelper = pRegistration.getJeiHelpers().getGuiHelper();
        pRegistration.addRecipeCategories(
                new AtomizerRecipeCategory(guiHelper),
                new CombinerRecipeCategory(guiHelper),
                new CompactorRecipeCategory(guiHelper),
                new DissolverRecipeCategory(guiHelper),
                new FissionRecipeCategory(guiHelper),
                new FusionRecipeCategory(guiHelper),
                new LiquifierRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration pRegistration) {
        pRegistration.addRecipeClickArea(AtomizerScreen.class, 78, 35, 30, 9, RecipeTypes.ATOMIZER);
        pRegistration.addRecipeClickArea(CombinerScreen.class, 87, 35, 30, 9, RecipeTypes.COMBINER);
        pRegistration.addRecipeClickArea(CompactorScreen.class, 78, 54, 30, 9, RecipeTypes.COMPACTOR);
        pRegistration.addRecipeClickArea(DissolverScreen.class, 69, 35, 30, 9, RecipeTypes.DISSOLVER);
        pRegistration.addRecipeClickArea(FissionControllerScreen.class, 74, 35, 30, 9, RecipeTypes.FISSION);
        pRegistration.addRecipeClickArea(FusionControllerScreen.class, 78, 35, 30, 9, RecipeTypes.FUSION);
        pRegistration.addRecipeClickArea(LiquifierScreen.class, 79, 35, 30, 9, RecipeTypes.LIQUIFIER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration pRegistration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        pRegistration.addRecipes(RecipeTypes.ATOMIZER, recipeManager.getAllRecipesFor(RecipeRegistry.ATOMIZER_TYPE.get()));
        pRegistration.addRecipes(RecipeTypes.COMBINER, recipeManager.getAllRecipesFor(RecipeRegistry.COMBINER_TYPE.get()));
        pRegistration.addRecipes(RecipeTypes.COMPACTOR, recipeManager.getAllRecipesFor(RecipeRegistry.COMPACTOR_TYPE.get()));
        pRegistration.addRecipes(RecipeTypes.DISSOLVER, recipeManager.getAllRecipesFor(RecipeRegistry.DISSOLVER_TYPE.get()));
        pRegistration.addRecipes(RecipeTypes.FISSION, recipeManager.getAllRecipesFor(RecipeRegistry.FISSION_TYPE.get()));
        pRegistration.addRecipes(RecipeTypes.FUSION, recipeManager.getAllRecipesFor(RecipeRegistry.FUSION_TYPE.get()));
        pRegistration.addRecipes(RecipeTypes.LIQUIFIER, recipeManager.getAllRecipesFor(RecipeRegistry.LIQUIFIER_TYPE.get()));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration pRegistration) {
        pRegistration.addRecipeTransferHandler(new CombinerTransferPacket.TransferHandler(), RecipeTypes.COMBINER);
        pRegistration.addRecipeTransferHandler(new CompactorTransferPacket.TransferHandler(), RecipeTypes.COMPACTOR);
        pRegistration.addRecipeTransferHandler(new DissolverTransferPacket.TransferHandler(), RecipeTypes.DISSOLVER);
        pRegistration.addRecipeTransferHandler(new FissionTransferPacket.TransferHandler(), RecipeTypes.FISSION);
        pRegistration.addRecipeTransferHandler(new FusionTransferPacket.TransferHandler(), RecipeTypes.FUSION);
        pRegistration.addRecipeTransferHandler(new LiquifierTransferPacket.TransferHandler(), RecipeTypes.LIQUIFIER);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration pRegistration) {
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.ATOMIZER.get().asItem()), RecipeTypes.ATOMIZER);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.COMBINER.get().asItem()), RecipeTypes.COMBINER);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.COMPACTOR.get().asItem()), RecipeTypes.COMPACTOR);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.DISSOLVER.get().asItem()), RecipeTypes.DISSOLVER);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.FISSION_CONTROLLER.get().asItem()), RecipeTypes.FISSION);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.FUSION_CONTROLLER.get().asItem()), RecipeTypes.FUSION);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.LIQUIFIER.get().asItem()), RecipeTypes.LIQUIFIER);
    }
}
