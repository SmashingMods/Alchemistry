package al132.alchemistry.compat.jei;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Ref;
import al132.alchemistry.blocks.atomizer.AtomizerScreen;
import al132.alchemistry.blocks.combiner.CombinerScreen;
import al132.alchemistry.blocks.dissolver.DissolverContainer;
import al132.alchemistry.blocks.dissolver.DissolverScreen;
import al132.alchemistry.blocks.evaporator.EvaporatorScreen;
import al132.alchemistry.blocks.fission.FissionContainer;
import al132.alchemistry.blocks.fission.FissionScreen;
import al132.alchemistry.blocks.liquifier.LiquifierContainer;
import al132.alchemistry.blocks.liquifier.LiquifierScreen;
import al132.alchemistry.compat.jei.category.*;
import al132.alchemistry.recipes.ModRecipes;
import al132.chemlib.chemistry.ElementRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    public static final String ATOMIZER_CATEGORY = "atomizer_recipe";
    public static final String COMBINER_CATEGORY = "combiner_recipe";
    public static final String DISSOLVER_CATEGORY = "dissolver_recipe";
    public static final String EVAPORATOR_CATEGORY = "evaporator_recipe";
    public static final String FISSION_CATEGORY = "fission_recipe";
    public static final String LIQUIFIER_CATEGORY = "liquifier_recipe";

    public static final ResourceLocation ATOMIZER_RESOURCE = new ResourceLocation(Alchemistry.data.MODID, ATOMIZER_CATEGORY);
    public static final ResourceLocation COMBINER_RESOURCE = new ResourceLocation(Alchemistry.data.MODID, COMBINER_CATEGORY);
    public static final ResourceLocation DISSOLVER_RESOURCE = new ResourceLocation(Alchemistry.data.MODID, DISSOLVER_CATEGORY);
    public static final ResourceLocation EVAPORATOR_RESOURCE = new ResourceLocation(Alchemistry.data.MODID, EVAPORATOR_CATEGORY);
    public static final ResourceLocation FISSION_RESOURCE = new ResourceLocation(Alchemistry.data.MODID, FISSION_CATEGORY);
    public static final ResourceLocation LIQUIFIER_RESOURCE = new ResourceLocation(Alchemistry.data.MODID, LIQUIFIER_CATEGORY);


    public JEIIntegration() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Alchemistry.data.MODID, "alchemistry");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper guiHelper = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(
                new AtomizerRecipeCategory(guiHelper),
                new CombinerRecipeCategory(guiHelper),
                new DissolverRecipeCategory(guiHelper),
                new EvaporatorRecipeCategory(guiHelper),
                new FissionRecipeCategory(guiHelper),
                new LiquifierRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        reg.addRecipes(ModRecipes.atomizerRecipes, ATOMIZER_RESOURCE);
        reg.addRecipes(ModRecipes.combinerRecipes, COMBINER_RESOURCE);
        reg.addRecipes(ModRecipes.dissolverRecipes, DISSOLVER_RESOURCE);
        reg.addRecipes(ModRecipes.evaporatorRecipes, EVAPORATOR_RESOURCE);
        reg.addRecipes(ModRecipes.fissionRecipes, FISSION_RESOURCE);
        reg.addRecipes(ModRecipes.liquifierRecipes, LIQUIFIER_RESOURCE);

        ElementRegistry.elements.forEach((key, value) -> reg.addIngredientInfo(
                new ItemStack(value), VanillaTypes.ITEM, "alchemistry.jei.elements.description"));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration reg) {
        reg.addRecipeTransferHandler(new CombinerTransferHandler(), COMBINER_RESOURCE);
        reg.addRecipeTransferHandler(DissolverContainer.class, DISSOLVER_RESOURCE, 0, 1, 11, 36);
        reg.addRecipeTransferHandler(FissionContainer.class, FISSION_RESOURCE, 0, 1, 3, 36);
        reg.addRecipeTransferHandler(LiquifierContainer.class, LIQUIFIER_RESOURCE, 0, 1, 1, 36);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(Ref.atomizer), ATOMIZER_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Ref.combiner), COMBINER_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Ref.dissolver), DISSOLVER_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Ref.evaporator), EVAPORATOR_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Ref.fissionController), FISSION_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Ref.liquifier), LIQUIFIER_RESOURCE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(AtomizerScreen.class, 73, 54, 39, 23, ATOMIZER_RESOURCE);
        reg.addRecipeClickArea(CombinerScreen.class, 100, 20, 35, 33, COMBINER_RESOURCE);
        reg.addRecipeClickArea(DissolverScreen.class, 86, 50, 17, 33, DISSOLVER_RESOURCE);
        reg.addRecipeClickArea(EvaporatorScreen.class, 73, 54, 39, 23, EVAPORATOR_RESOURCE);
        reg.addRecipeClickArea(FissionScreen.class, 73, 54, 39, 23, FISSION_RESOURCE);
        reg.addRecipeClickArea(LiquifierScreen.class, 73, 54, 39, 23, LIQUIFIER_RESOURCE);
    }
}
