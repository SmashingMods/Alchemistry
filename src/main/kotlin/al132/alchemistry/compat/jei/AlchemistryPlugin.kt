package al132.alchemistry.compat.jei

import al132.alchemistry.Reference
import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.client.*
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.ATOMIZER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.COMBINER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.DISSOLVER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.ELECTROLYZER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.EVAPORATOR
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.LIQUIFIER
import al132.alchemistry.compat.jei.atomizer.AtomizerRecipeCategory
import al132.alchemistry.compat.jei.atomizer.AtomizerRecipeWrapper
import al132.alchemistry.compat.jei.combiner.CombinerRecipeCategory
import al132.alchemistry.compat.jei.combiner.CombinerRecipeWrapper
import al132.alchemistry.compat.jei.combiner.CombinerTransferHandler
import al132.alchemistry.compat.jei.dissolver.DissolverRecipeCategory
import al132.alchemistry.compat.jei.dissolver.DissolverRecipeWrapper
import al132.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeCategory
import al132.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeWrapper
import al132.alchemistry.compat.jei.evaporator.EvaporatorRecipeCategory
import al132.alchemistry.compat.jei.evaporator.EvaporatorRecipeWrapper
import al132.alchemistry.compat.jei.liquifier.LiquifierRecipeCategory
import al132.alchemistry.compat.jei.liquifier.LiquifierRecipeWrapper
import al132.alchemistry.recipes.*
import al132.alib.utils.extensions.toStack
import mezz.jei.api.*
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry


@JEIPlugin
class AlchemistryPlugin : IModPlugin {

    companion object {
        lateinit var jeiHelpers: IJeiHelpers
        lateinit var recipeRegistry: IRecipeRegistry
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime?) {
        recipeRegistry = jeiRuntime!!.recipeRegistry
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration?) {
        registry?.jeiHelpers?.guiHelper?.let { guiHelper ->
            registry.addRecipeCategories(
                    DissolverRecipeCategory(guiHelper),
                    CombinerRecipeCategory(guiHelper),
                    ElectrolyzerRecipeCategory(guiHelper),
                    EvaporatorRecipeCategory(guiHelper),
                    AtomizerRecipeCategory(guiHelper),
                    LiquifierRecipeCategory(guiHelper)
            )
        }
    }

    override fun register(registry: IModRegistry?) {
        jeiHelpers = registry!!.jeiHelpers

        registry.handleRecipes(DissolverRecipe::class.java,
                { recipe -> DissolverRecipeWrapper(recipe) },
                DISSOLVER)
        registry.handleRecipes(ElectrolyzerRecipe::class.java,
                { recipe -> ElectrolyzerRecipeWrapper(recipe) },
                ELECTROLYZER)
        registry.handleRecipes(CombinerRecipe::class.java,
                { recipe -> CombinerRecipeWrapper(recipe) },
                COMBINER)
        registry.handleRecipes(EvaporatorRecipe::class.java,
                { recipe -> EvaporatorRecipeWrapper(recipe) },
                EVAPORATOR)
        registry.handleRecipes(AtomizerRecipe::class.java,
                { recipe -> AtomizerRecipeWrapper(recipe) },
                ATOMIZER)
        registry.handleRecipes(LiquifierRecipe::class.java,
                { recipe -> LiquifierRecipeWrapper(recipe) },
                LIQUIFIER)

        registry.addRecipes(ModRecipes.dissolverRecipes.map { DissolverRecipeWrapper(it) }, DISSOLVER)
        registry.addRecipes(ModRecipes.combinerRecipes.map { CombinerRecipeWrapper(it) }, COMBINER)
        registry.addRecipes(ModRecipes.electrolyzerRecipes.map { ElectrolyzerRecipeWrapper(it) }, ELECTROLYZER)
        registry.addRecipes(ModRecipes.evaporatorRecipes.map { EvaporatorRecipeWrapper(it) }, EVAPORATOR)
        registry.addRecipes(ModRecipes.atomizerRecipes.map { AtomizerRecipeWrapper(it) }, ATOMIZER)
        registry.addRecipes(ModRecipes.liquifierRecipes.map { LiquifierRecipeWrapper(it) }, LIQUIFIER)

        registry.addRecipeClickArea(GuiChemicalDissolver::class.java, 86, 50, 17, 33, AlchemistryRecipeUID.DISSOLVER)
        registry.addRecipeClickArea(GuiChemicalCombiner::class.java, 100, 20, 35, 33, AlchemistryRecipeUID.COMBINER)
        registry.addRecipeClickArea(GuiElectrolyzer::class.java, 73, 58, 39, 23, AlchemistryRecipeUID.ELECTROLYZER)
        registry.addRecipeClickArea(GuiEvaporator::class.java, 73, 54, 39, 23, AlchemistryRecipeUID.EVAPORATOR)
        registry.addRecipeClickArea(GuiAtomizer::class.java, 73, 54, 39, 23, AlchemistryRecipeUID.ATOMIZER)
        registry.addRecipeClickArea(GuiLiquifier::class.java, 73, 54, 39, 23, AlchemistryRecipeUID.LIQUIFIER)


        registry.addRecipeCatalyst(ModBlocks.chemical_dissolver.toStack(), AlchemistryRecipeUID.DISSOLVER)
        registry.addRecipeCatalyst(ModBlocks.chemical_combiner.toStack(), AlchemistryRecipeUID.COMBINER)
        registry.addRecipeCatalyst(ModBlocks.electrolyzer.toStack(), AlchemistryRecipeUID.ELECTROLYZER)
        registry.addRecipeCatalyst(ModBlocks.evaporator.toStack(), AlchemistryRecipeUID.EVAPORATOR)
        registry.addRecipeCatalyst(ModBlocks.atomizer.toStack(), AlchemistryRecipeUID.ATOMIZER)
        registry.addRecipeCatalyst(ModBlocks.liquifier.toStack(), AlchemistryRecipeUID.LIQUIFIER)


        val transferRegistry: IRecipeTransferRegistry = registry.recipeTransferRegistry
        transferRegistry.addRecipeTransferHandler(CombinerTransferHandler(), COMBINER)

        //transferRegistry.addRecipeTransferHandler(ContainerChemicalCombiner::class.java, COMBINER, 0, 9, 10, 36)
        transferRegistry.addRecipeTransferHandler(ContainerChemicalDissolver::class.java, DISSOLVER, 0, 1, 11, 36)
        transferRegistry.addRecipeTransferHandler(ContainerLiquifier::class.java, LIQUIFIER, 0, 1, 1, 36)
        transferRegistry.addRecipeTransferHandler(ContainerElectrolyzer::class.java, ELECTROLYZER, 0, 1, 1, 36)

    }
}

object AlchemistryRecipeUID {
    val COMBINER = Reference.MODID + ".combiner"
    val DISSOLVER = Reference.MODID + ".dissolver"
    val ELECTROLYZER = Reference.MODID + ".electrolyzer"
    val EVAPORATOR = Reference.MODID + ".evaporator"
    val ATOMIZER = Reference.MODID + ".atomizer"
    val LIQUIFIER = Reference.MODID + ".liquifier"
}

abstract class AlchemistryRecipeWrapper<out R>(val recipe: R) : IRecipeWrapper


abstract class AlchemistryRecipeCategory<T : IRecipeWrapper>(private val background: IDrawable, unlocalizedName: String) :
        IRecipeCategory<T> {

    val localizedName: String = Translator.translateToLocal(unlocalizedName)

    override fun getBackground(): IDrawable = background

    override fun getModName() = Reference.MODID
}