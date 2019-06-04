package al132.alchemistry.compat.jei.combiner

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.CombinerRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.darkhax.gamestages.GameStageHelper
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Loader
import java.awt.Color

class CombinerRecipeWrapper(recipe: CombinerRecipe) : AlchemistryRecipeWrapper<CombinerRecipe>(recipe) {

    override fun drawInfo(minecraft: Minecraft?, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {

        var y = 62
        val stage = recipe.gamestage
        if (Loader.isModLoaded("gamestages") && stage.isNotEmpty()) {
            val color = if (GameStageHelper.hasStage(minecraft!!.player, recipe.gamestage)) Color(0, 153, 51) else Color.RED
            minecraft.fontRenderer?.drawString("Gamestage: $stage", 2, y, color.rgb)
        }
    }

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.inputs)
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output)
    }
}