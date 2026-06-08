package com.smashingmods.alchemistry.client.container;

import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tier-2 test for the liquifier branch of {@link RecipeDisplayUtil#getTarget(com.smashingmods.alchemylib.api.recipe.ProcessingRecipe)}
 * and {@link RecipeDisplayUtil#getRecipeInputByIndex(com.smashingmods.alchemylib.api.recipe.ProcessingRecipe, int)}.
 * A {@link LiquifierRecipe}'s display input is its ingredient's first stack -- so the guard must return that stack
 * when the ingredient resolves to at least one item, and {@code ItemStack.EMPTY} only when the ingredient is empty.
 * If the {@code isEmpty()} ternary is the wrong way round it returns {@code EMPTY} for a populated recipe (and would
 * index a guaranteed-empty list), which is what these assertions pin. The recipe holds an {@link IngredientStack},
 * so this extends {@link BootstrappedTest} and builds the input from a vanilla {@code Items.*}; the output fluid is
 * never read by either method but is given a real fluid so the recipe is well-formed.
 */
class RecipeDisplayUtilTest extends BootstrappedTest {

    private static final int INPUT_COUNT = 3;

    @Test
    void getTarget_liquifierReturnsInputStack() {
        LiquifierRecipe recipe = liquifierRecipe();

        ItemStack target = RecipeDisplayUtil.getTarget(recipe);

        assertFalse(target.isEmpty(), "liquifier target must be the populated input stack, not EMPTY");
        assertEquals(Items.STONE, target.getItem());
        assertEquals(INPUT_COUNT, target.getCount());
    }

    @Test
    void getRecipeInputByIndex_liquifierReturnsInputStack() {
        LiquifierRecipe recipe = liquifierRecipe();

        ItemStack input = RecipeDisplayUtil.getRecipeInputByIndex(recipe, 0);

        assertFalse(input.isEmpty(), "liquifier input-by-index must be the populated input stack, not EMPTY");
        assertEquals(Items.STONE, input.getItem());
        assertEquals(INPUT_COUNT, input.getCount());
    }

    private static LiquifierRecipe liquifierRecipe() {
        return new LiquifierRecipe(
                ResourceLocation.fromNamespaceAndPath("alchemistry", "test/liquifier"),
                "liquifier",
                new IngredientStack(Items.STONE, INPUT_COUNT),
                new FluidStack(Fluids.WATER, 1000));
    }
}
