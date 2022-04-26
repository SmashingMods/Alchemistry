package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.category.AtomizerRecipeCategory;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerMenu;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerScreen;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

//@JeiPlugin
public class JEI implements IModPlugin {

    public static final String ATOMIZER_CATEGORY = "atomizer_recipe";
    public static final String COMBINER_CATEGORY = "combiner_recipe";

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Alchemistry.MODID, "alchemistry");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration pRegistration) {
        IGuiHelper guiHelper = pRegistration.getJeiHelpers().getGuiHelper();
        pRegistration.addRecipeCategories(
            new AtomizerRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration pRegistration) {


//        pRegistration.addRecipes(RecipeTypes.ATOMIZER, AtomizerRegistry.getRecipes());

        ElementRegistry.elements.forEach((key, value) -> {
            Objects.requireNonNull(value.getRegistryName());
            pRegistration.addIngredientInfo(new ItemStack(value), VanillaTypes.ITEM_STACK, new TranslatableComponent(String.format("%s.jei.elements.%s.description", Alchemistry.MODID, value.getRegistryName().getPath())));
        });
    }

    @Override
    public void registerRecipeTransferHandlers(@Nonnull IRecipeTransferRegistration pRegistration) {
        pRegistration.addRecipeTransferHandler(AtomizerMenu.class, new AtomizerRecipeCategory().getRecipeType(), 0, 1, 1, 36);
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration pRegistration) {
        pRegistration.addRecipeClickArea(AtomizerScreen.class, 73, 54, 39, 23, new AtomizerRecipeCategory().getRecipeType());
    }
}
