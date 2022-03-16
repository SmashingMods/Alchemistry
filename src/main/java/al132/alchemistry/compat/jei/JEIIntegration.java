package al132.alchemistry.compat.jei;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Registration;
import al132.alchemistry.blocks.atomizer.AtomizerRegistry;
import al132.alchemistry.blocks.atomizer.AtomizerScreen;
import al132.alchemistry.blocks.combiner.CombinerRegistry;
import al132.alchemistry.blocks.combiner.CombinerScreen;
import al132.alchemistry.blocks.dissolver.DissolverContainer;
import al132.alchemistry.blocks.dissolver.DissolverRegistry;
import al132.alchemistry.blocks.dissolver.DissolverScreen;
import al132.alchemistry.blocks.evaporator.EvaporatorRegistry;
import al132.alchemistry.blocks.evaporator.EvaporatorScreen;
import al132.alchemistry.blocks.fission.FissionContainer;
import al132.alchemistry.blocks.fission.FissionRegistry;
import al132.alchemistry.blocks.fission.FissionScreen;
import al132.alchemistry.blocks.liquifier.LiquifierContainer;
import al132.alchemistry.blocks.liquifier.LiquifierRegistry;
import al132.alchemistry.blocks.liquifier.LiquifierScreen;
import al132.alchemistry.compat.jei.category.*;
import al132.chemlib.chemistry.ElementRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


@JeiPlugin
public class JEIIntegration implements IModPlugin {

    public static final String ATOMIZER_CATEGORY = "atomizer_recipe";
    public static final String COMBINER_CATEGORY = "combiner_recipe";
    public static final String DISSOLVER_CATEGORY = "dissolver_recipe";
    public static final String EVAPORATOR_CATEGORY = "evaporator_recipe";
    public static final String FISSION_CATEGORY = "fission_recipe";
    public static final String LIQUIFIER_CATEGORY = "liquifier_recipe";

    public static final ResourceLocation ATOMIZER_RESOURCE = new ResourceLocation(Alchemistry.MODID, ATOMIZER_CATEGORY);
    public static final ResourceLocation COMBINER_RESOURCE = new ResourceLocation(Alchemistry.MODID, COMBINER_CATEGORY);
    public static final ResourceLocation DISSOLVER_RESOURCE = new ResourceLocation(Alchemistry.MODID, DISSOLVER_CATEGORY);
    public static final ResourceLocation EVAPORATOR_RESOURCE = new ResourceLocation(Alchemistry.MODID, EVAPORATOR_CATEGORY);
    public static final ResourceLocation FISSION_RESOURCE = new ResourceLocation(Alchemistry.MODID, FISSION_CATEGORY);
    public static final ResourceLocation LIQUIFIER_RESOURCE = new ResourceLocation(Alchemistry.MODID, LIQUIFIER_CATEGORY);


    public JEIIntegration() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Alchemistry.MODID, "alchemistry");
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
        Level world = Minecraft.getInstance().level;

        //ModRecipes.filterEmptyDissolverRecipes();
        //ModRecipes.initEvaporatorRecipes();
        //ModRecipes.initDissolverRecipes();//before combiner recipes, so combiner can use reversible recipes
        //ModRecipes.initCombinerRecipes();
        //ModRecipes.initAtomizerRecipes(); //before liquifier recipes, for reversible recipes
        //ModRecipes.initLiquifierRecipes();
        //ModRecipes.initFissionRecipes();
        reg.addRecipes(AtomizerRegistry.getRecipes(world), ATOMIZER_RESOURCE);
        reg.addRecipes(CombinerRegistry.getRecipes(world), COMBINER_RESOURCE);
        reg.addRecipes(DissolverRegistry.getRecipes(world), DISSOLVER_RESOURCE);
        reg.addRecipes(EvaporatorRegistry.getRecipes(world), EVAPORATOR_RESOURCE);
        reg.addRecipes(FissionRegistry.getRecipes(world), FISSION_RESOURCE);
        reg.addRecipes(LiquifierRegistry.getRecipes(world), LIQUIFIER_RESOURCE);

        ElementRegistry.elements.forEach((key, value) -> reg.addIngredientInfo(
                new ItemStack(value), VanillaTypes.ITEM, new TextComponent("alchemistry.jei.elements.description")));
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
        reg.addRecipeCatalyst(new ItemStack(Registration.ATOMIZER_BLOCK.get()), ATOMIZER_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Registration.COMBINER_BLOCK.get()), COMBINER_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Registration.DISSOLVER_BLOCK.get()), DISSOLVER_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Registration.EVAPORATOR_BLOCK.get()), EVAPORATOR_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Registration.FISSION_CONTROLLER_BLOCK.get()), FISSION_RESOURCE);
        reg.addRecipeCatalyst(new ItemStack(Registration.LIQUIFIER_BLOCK.get()), LIQUIFIER_RESOURCE);
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
