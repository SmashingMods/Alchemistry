package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.category.*;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration pRegistration) {
        LOGGER.info("[Alchemistry JEI] registerCategories called");
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
    public void registerRecipes(IRecipeRegistration pRegistration) {
        LOGGER.info("[Alchemistry JEI] registerRecipes called");
        Minecraft minecraft = Minecraft.getInstance();
        RecipeManager recipeManager = null;

        if (minecraft.level != null) {
            recipeManager = minecraft.level.getRecipeManager();
        } else {
            ClientPacketListener connection = minecraft.getConnection();
            if (connection != null) {
                recipeManager = connection.getRecipeManager();
            }
        }

        if (recipeManager == null) {
            LOGGER.warn("[Alchemistry JEI] RecipeManager is null during registerRecipes. Skipping JEI recipe registration.");
            return;
        }

        // Diagnostic: log all recipe types present in RecipeManager
        java.util.Map<String, Long> typeCount = recipeManager.getRecipes().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                h -> {
                    try { return h.value().getType().toString(); } catch (Exception e) { return "unknown"; }
                },
                java.util.stream.Collectors.counting()
            ));
        LOGGER.info("[Alchemistry JEI] RecipeManager total={}, type breakdown:", recipeManager.getRecipes().size());
        typeCount.entrySet().stream()
            .filter(e -> e.getKey().contains("alchemistry"))
            .forEach(e -> LOGGER.info("[Alchemistry JEI]   type='{}' count={}", e.getKey(), e.getValue()));
        LOGGER.info("[Alchemistry JEI] ATOMIZER_TYPE.get()='{}' identity={}", RecipeRegistry.ATOMIZER_TYPE.get(), System.identityHashCode(RecipeRegistry.ATOMIZER_TYPE.get()));
        // Also check: does any recipe have a type equal to ATOMIZER_TYPE.get()?
        long atomizerByClass = recipeManager.getRecipes().stream()
            .filter(h -> h.value() instanceof com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe)
            .count();
        LOGGER.info("[Alchemistry JEI] AtomizerRecipe instances by class: {}", atomizerByClass);

        var atomizer = RecipeRegistry.getAtomizerRecipes(minecraft.level).stream().toList();
        var combiner = RecipeRegistry.getCombinerRecipes(minecraft.level).stream().toList();
        var compactor = RecipeRegistry.getCompactorRecipes(minecraft.level).stream().toList();
        var dissolver = RecipeRegistry.getDissolverRecipes(minecraft.level).stream().toList();
        var fission = RecipeRegistry.getFissionRecipes(minecraft.level).stream().toList();
        var fusion = RecipeRegistry.getFusionRecipes(minecraft.level).stream().toList();
        var liquifier = RecipeRegistry.getLiquifierRecipes(minecraft.level).stream().toList();
        
        // If still 0, try by class directly
        if (atomizer.isEmpty()) {
            var atomizerDirect = recipeManager.getRecipes().stream()
                .filter(h -> h.value() instanceof com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe)
                .map(h -> (com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe) h.value())
                .toList();
            LOGGER.info("[Alchemistry JEI] Direct class scan found {} AtomizerRecipes", atomizerDirect.size());
            if (!atomizerDirect.isEmpty()) {
                LOGGER.info("[Alchemistry JEI] Sample atomizer type='{}' identity={}", atomizerDirect.get(0).getType(), System.identityHashCode(atomizerDirect.get(0).getType()));
            }
        }

        pRegistration.addRecipes(RecipeTypes.ATOMIZER, atomizer);
        pRegistration.addRecipes(RecipeTypes.COMBINER, combiner);
        pRegistration.addRecipes(RecipeTypes.COMPACTOR, compactor);
        pRegistration.addRecipes(RecipeTypes.DISSOLVER, dissolver);
        pRegistration.addRecipes(RecipeTypes.FISSION, fission);
        pRegistration.addRecipes(RecipeTypes.FUSION, fusion);
        pRegistration.addRecipes(RecipeTypes.LIQUIFIER, liquifier);

        int totalRecipes = atomizer.size() + combiner.size() + compactor.size() + dissolver.size() + fission.size() + fusion.size() + liquifier.size();
        LOGGER.info("[Alchemistry JEI] Registered recipes -> atomizer={}, combiner={}, compactor={}, dissolver={}, fission={}, fusion={}, liquifier={}, total={}",
                atomizer.size(), combiner.size(), compactor.size(), dissolver.size(), fission.size(), fusion.size(), liquifier.size(), totalRecipes);

        if (totalRecipes == 0) {
            LOGGER.error("[Alchemistry JEI] No machine recipes were available in RecipeManager. Check datapack loading and client recipe sync.");
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration pRegistration) {
        LOGGER.info("[Alchemistry JEI] registerRecipeCatalysts called");
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.ATOMIZER.get().asItem()), RecipeTypes.ATOMIZER);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.COMBINER.get().asItem()), RecipeTypes.COMBINER);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.COMPACTOR.get().asItem()), RecipeTypes.COMPACTOR);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.DISSOLVER.get().asItem()), RecipeTypes.DISSOLVER);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.FISSION_CONTROLLER.get().asItem()), RecipeTypes.FISSION);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.FUSION_CONTROLLER.get().asItem()), RecipeTypes.FUSION);
        pRegistration.addRecipeCatalyst(new ItemStack(BlockRegistry.LIQUIFIER.get().asItem()), RecipeTypes.LIQUIFIER);
    }
}
