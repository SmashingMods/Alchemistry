package com.smashingmods.alchemistry.client.container;

import com.mojang.datafixers.util.Either;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 test for the static display helpers in {@link RecipeDisplayUtil}.
 *
 * <p>{@link RecipeDisplayUtil#getTarget(com.smashingmods.alchemylib.api.recipe.ProcessingRecipe)} and
 * {@link RecipeDisplayUtil#getRecipeInputByIndex(com.smashingmods.alchemylib.api.recipe.ProcessingRecipe, int)}:
 * a {@link LiquifierRecipe}'s display input is its ingredient's first stack -- so the guard must return that stack
 * when the ingredient resolves to at least one item, and {@code ItemStack.EMPTY} only when the ingredient is empty.
 * If the {@code isEmpty()} ternary is the wrong way round it returns {@code EMPTY} for a populated recipe (and would
 * index a guaranteed-empty list), which is what those assertions pin.</p>
 *
 * <p>{@link RecipeDisplayUtil#getRecipeInputByIndex(com.smashingmods.alchemylib.api.recipe.ProcessingRecipe, int)}
 * also has to survive an ingredient that resolves to zero stacks (an empty tag client-side): the combiner and
 * compactor branches feed {@code toStacks().size()} into {@code new Random().ints(0, size)}, and {@code ints(0, 0)}
 * throws {@link IllegalArgumentException}. Those branches must instead fall back to {@code ItemStack.EMPTY}.</p>
 *
 * <p>{@link RecipeDisplayUtil#getSearchablePair(com.smashingmods.alchemylib.api.recipe.ProcessingRecipe)} builds the
 * searchable string the recipe selector filters on. It must use the item's <em>rendered</em> name
 * ({@code Component.getString()}), not the debug form ({@code Component.toString()}, which is {@code translation{...}}
 * /{@code literal{...}}), or typing the item's name would never match.</p>
 *
 * <p>Recipes are built from vanilla {@code Items.*}/{@code Fluids.*} (no mod-Item construction) under
 * {@link BootstrappedTest}'s bootstrap so {@link ItemStack}, {@link FluidStack} and {@link IngredientStack} are
 * usable.</p>
 */
class RecipeDisplayUtilTest extends BootstrappedTest {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("alchemistry", "test/recipe");
    private static final String GROUP = "test";
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

    @Test
    void getSearchablePair_combinerUsesRenderedOutputName() {
        CombinerRecipe recipe = new CombinerRecipe(ID, GROUP,
                Set.of(new IngredientStack(Items.IRON_INGOT)),
                new ItemStack(Items.STONE));

        Pair<ResourceLocation, String> pair = RecipeDisplayUtil.getSearchablePair(recipe);

        // The rendered name is what a player would read and type; the debug form (Component.toString()) wraps it in
        // braces, so the absence of '{' is exactly the discriminator the .toString() bug would have failed.
        assertFalse(pair.getRight().contains("{"),
                "searchable string must be the rendered name, not the Component debug form: " + pair.getRight());
        assertEquals(Items.STONE.getName().getString().toLowerCase(), pair.getRight());
    }

    @Test
    void getRecipeInputByIndex_combinerEmptyIngredientReturnsEmpty() {
        Set<IngredientStack> inputs = new LinkedHashSet<>();
        inputs.add(emptyIngredientStack());
        CombinerRecipe recipe = new CombinerRecipe(ID, GROUP, inputs, new ItemStack(Items.STONE));

        ItemStack input = assertDoesNotThrow(() -> RecipeDisplayUtil.getRecipeInputByIndex(recipe, 0),
                "an ingredient resolving to zero stacks must not crash Random.ints(0, 0)");

        assertTrue(input.isEmpty(), "an ingredient resolving to zero stacks must yield EMPTY");
    }

    @Test
    void getRecipeInputByIndex_compactorEmptyIngredientReturnsEmpty() {
        CompactorRecipe recipe = new CompactorRecipe(ID, GROUP, emptyIngredientStack(), new ItemStack(Items.STONE));

        ItemStack input = assertDoesNotThrow(() -> RecipeDisplayUtil.getRecipeInputByIndex(recipe, 0),
                "an ingredient resolving to zero stacks must not crash Random.ints(0, 0)");

        assertTrue(input.isEmpty(), "an ingredient resolving to zero stacks must yield EMPTY");
    }

    private static LiquifierRecipe liquifierRecipe() {
        return new LiquifierRecipe(ID, GROUP,
                new IngredientStack(Items.STONE, INPUT_COUNT),
                new FluidStack(Fluids.WATER, 1000));
    }

    /**
     * An {@link IngredientStack} whose {@code toStacks()} is empty, standing in for an ingredient that resolves to
     * zero items at display time (e.g. a tag with no members on the client). It is tag-backed so the empty set is
     * legal: {@link Ingredient}'s constructor only rejects an empty <em>item</em>-backed set, and the
     * {@link IngredientStack} constructor reads the tag location off {@link HolderSet#unwrap()} without dereferencing
     * the (empty) contents.
     */
    private static IngredientStack emptyIngredientStack() {
        return new IngredientStack(Ingredient.of(emptyItemTagSet()));
    }

    private static HolderSet<Item> emptyItemTagSet() {
        TagKey<Item> key = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("alchemistry", "test/empty"));
        return new HolderSet<>() {
            @Override public Stream<Holder<Item>> stream() { return Stream.of(); }
            @Override public int size() { return 0; }
            @Override public boolean isBound() { return true; }
            @Override public Either<TagKey<Item>, List<Holder<Item>>> unwrap() { return Either.left(key); }
            @Override public Optional<Holder<Item>> getRandomElement(RandomSource random) { return Optional.empty(); }
            @Override public Holder<Item> get(int index) { throw new IndexOutOfBoundsException(index); }
            @Override public boolean contains(Holder<Item> holder) { return false; }
            @Override public boolean canSerializeIn(HolderOwner<Item> owner) { return true; }
            @Override public Optional<TagKey<Item>> unwrapKey() { return Optional.of(key); }
            @Override public Iterator<Holder<Item>> iterator() { return Collections.emptyIterator(); }
        };
    }
}
