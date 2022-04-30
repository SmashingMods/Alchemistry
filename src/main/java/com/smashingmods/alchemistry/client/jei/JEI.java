package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.category.AtomizerRecipeCategory;
import com.smashingmods.alchemistry.client.jei.category.CombinerRecipeCategory;
import com.smashingmods.alchemistry.common.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class JEI implements IModPlugin {

    public static final RecipeType<AtomizerRecipe> ATOMIZER = RecipeType.create(Alchemistry.MODID, "atomizer", AtomizerRecipe.class);
    public static final RecipeType<CombinerRecipe> COMBINER = RecipeType.create(Alchemistry.MODID, "combiner", CombinerRecipe.class);

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Alchemistry.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration pRegistration) {

        ClientLevel level = Minecraft.getInstance().level;
        Objects.requireNonNull(level);

        List<AtomizerRecipe> atomizerRecipes = level.getRecipeManager().getAllRecipesFor(RecipeRegistry.ATOMIZER_TYPE);
        List<CombinerRecipe> combinerRecipes = level.getRecipeManager().getAllRecipesFor(RecipeRegistry.COMBINER_TYPE);

        pRegistration.addRecipes(ATOMIZER, atomizerRecipes);
        pRegistration.addRecipes(COMBINER, combinerRecipes);

        ElementRegistry.elements.forEach((key, value) -> {
            Objects.requireNonNull(value.getRegistryName());
            pRegistration.addIngredientInfo(new ItemStack(value), VanillaTypes.ITEM_STACK, new TranslatableComponent(String.format("%s.jei.elements.%s.description", Alchemistry.MODID, value.getRegistryName().getPath())));
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration pRegistration) {
        IGuiHelper guiHelper = pRegistration.getJeiHelpers().getGuiHelper();
        pRegistration.addRecipeCategories(
                new AtomizerRecipeCategory(guiHelper),
                new CombinerRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration pRegistration) {
//        pRegistration.addRecipeTransferHandler(AtomizerMenu.class, new AtomizerRecipeCategory().getRecipeType(), 0, 1, 1, 36);
//        pRegistration.addRecipeTransferHandler(CombinerMenu.class, new CombinerRecipeCategory().getRecipeType(), 0, 1, 1, 36);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration pRegistration) {

        pRegistration.addGuiContainerHandler(CombinerScreen.class, new IGuiContainerHandler<>() {

            @Override
            public List<Rect2i> getGuiExtraAreas(CombinerScreen containerScreen) {
                return IGuiContainerHandler.super.getGuiExtraAreas(containerScreen);
            }
        });

//        pRegistration.addRecipeClickArea(AtomizerScreen.class, 73, 54, 39, 23, new AtomizerRecipeCategory().getRecipeType());
//        pRegistration.addRecipeClickArea(CombinerScreen.class, 73, 54, 39, 23, new CombinerRecipeCategory().getRecipeType());
    }
}
