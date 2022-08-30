package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipeSerializer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class RecipeRegistry {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static RecipeType<AtomizerRecipe> ATOMIZER_TYPE;
    public static RecipeType<CompactorRecipe> COMPACTOR_TYPE;
    public static RecipeType<CombinerRecipe> COMBINER_TYPE;
    public static RecipeType<DissolverRecipe> DISSOLVER_TYPE;
    public static RecipeType<FissionRecipe> FISSION_TYPE;
    public static RecipeType<FusionRecipe> FUSION_TYPE;
    public static RecipeType<LiquifierRecipe> LIQUIFIER_TYPE;

    public static final RegistryObject<AtomizerRecipeSerializer<AtomizerRecipe>> ATOMIZER_SERIALIZER
            = SERIALIZERS.register("atomizer", () -> new AtomizerRecipeSerializer<>(AtomizerRecipe::new));

    public static final RegistryObject<CompactorRecipeSerializer<CompactorRecipe>> COMPACTOR_SERIALIZER
            = SERIALIZERS.register("compactor", () -> new CompactorRecipeSerializer<>(CompactorRecipe::new));

    public static final RegistryObject<CombinerRecipeSerializer<CombinerRecipe>> COMBINER_SERIALIZER
            = SERIALIZERS.register("combiner", () -> new CombinerRecipeSerializer<>(CombinerRecipe::new));

    public static final RegistryObject<DissolverRecipeSerializer<DissolverRecipe>> DISSOLVER_SERIALIZER
            = SERIALIZERS.register("dissolver", () -> new DissolverRecipeSerializer<>(DissolverRecipe::new));

    public static final RegistryObject<FissionRecipeSerializer<FissionRecipe>> FISSION_SERIALIZER
            = SERIALIZERS.register("fission", () -> new FissionRecipeSerializer<>(FissionRecipe::new));

    public static final RegistryObject<FusionRecipeSerializer<FusionRecipe>> FUSION_SERIALIZER
            = SERIALIZERS.register("fusion", () -> new FusionRecipeSerializer<>(FusionRecipe::new));

    public static final RegistryObject<LiquifierRecipeSerializer<LiquifierRecipe>> LIQUIFIER_SERIALIZER
            = SERIALIZERS.register("liquifier", () -> new LiquifierRecipeSerializer<>(LiquifierRecipe::new));

    private static final Map<RecipeType<? extends Recipe<Inventory>>, List<? extends Recipe<Inventory>>> recipesMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<Inventory>> List<T> getRecipesByType(RecipeType<T> pRecipeType, Level pLevel) {
        if (recipesMap.get(pRecipeType) == null) {
            List<T> recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType().equals(pRecipeType))
                    .map(recipe -> (T) recipe)
                    .collect(Collectors.toList());
            recipesMap.put(pRecipeType, recipes);
        }
        return (List<T>) recipesMap.get(pRecipeType);
    }

    public static List<AtomizerRecipe> getAtomizerRecipes(Level pLevel) {
        return getRecipesByType(ATOMIZER_TYPE, pLevel);
    }

    public static List<CombinerRecipe> getCombinerRecipes(Level pLevel) {
        return getRecipesByType(COMBINER_TYPE, pLevel);
    }

    public static List<CompactorRecipe> getCompactorRecipes(Level pLevel) {
        return getRecipesByType(COMPACTOR_TYPE, pLevel);
    }

    public static List<DissolverRecipe> getDissolverRecipes(Level pLevel) {
        return getRecipesByType(DISSOLVER_TYPE, pLevel);
    }

    public static List<FissionRecipe> getFissionRecipes(Level pLevel) {
        return getRecipesByType(FISSION_TYPE, pLevel);
    }

    public static List<FusionRecipe> getFusionRecipes(Level pLevel) {
        return getRecipesByType(FUSION_TYPE, pLevel);
    }

    public static List<LiquifierRecipe> getLiquifierRecipes(Level pLevel) {
        return getRecipesByType(LIQUIFIER_TYPE, pLevel);
    }

    public static Optional<AtomizerRecipe> getAtomizerRecipe(Predicate<AtomizerRecipe> pPredicate, Level pLevel) {
        return getAtomizerRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static Optional<CombinerRecipe> getCombinerRecipe(Predicate<CombinerRecipe> pPredicate, Level pLevel) {
        return getCombinerRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static Optional<CompactorRecipe> getCompactorRecipe(Predicate<CompactorRecipe> pPredicate, Level pLevel) {
        return getCompactorRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static Optional<DissolverRecipe> getDissolverRecipe(Predicate<DissolverRecipe> pPredicate, Level pLevel) {
        return getDissolverRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static Optional<FissionRecipe> getFissionRecipe(Predicate<FissionRecipe> pPredicate, Level pLevel) {
        return getFissionRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static Optional<FusionRecipe> getFusionRecipe(Predicate<FusionRecipe> pPredicate, Level pLevel) {
        return getFusionRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static Optional<LiquifierRecipe> getLiquifierRecipe(Predicate<LiquifierRecipe> pPredicate, Level pLevel) {
        return getLiquifierRecipes(pLevel).stream().filter(pPredicate).findFirst();
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
