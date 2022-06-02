package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.evaporator.EvaporatorRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipeSerializer;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.evaporator.EvaporatorRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
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
import java.util.stream.Collectors;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class RecipeRegistry {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static RecipeType<AtomizerRecipe> ATOMIZER_TYPE;
    public static RecipeType<CompactorRecipe> COMPACTOR_TYPE;
    public static RecipeType<CombinerRecipe> COMBINER_TYPE;
    public static RecipeType<DissolverRecipe> DISSOLVER_TYPE;
    public static RecipeType<EvaporatorRecipe> EVAPORATOR_TYPE;
    public static RecipeType<FissionRecipe> FISSION_TYPE;
    public static RecipeType<LiquifierRecipe> LIQUIFIER_TYPE;

    public static final RegistryObject<AtomizerRecipeSerializer<AtomizerRecipe>> ATOMIZER_SERIALIZER
            = SERIALIZERS.register("atomizer", () -> new AtomizerRecipeSerializer<>(AtomizerRecipe::new));

    public static final RegistryObject<CompactorRecipeSerializer<CompactorRecipe>> COMPACTOR_SERIALIZER
            = SERIALIZERS.register("compactor", () -> new CompactorRecipeSerializer<>(CompactorRecipe::new));

    public static final RegistryObject<CombinerRecipeSerializer<CombinerRecipe>> COMBINER_SERIALIZER
            = SERIALIZERS.register("combiner", () -> new CombinerRecipeSerializer<>(CombinerRecipe::new));

    public static final RegistryObject<DissolverRecipeSerializer<DissolverRecipe>> DISSOLVER_SERIALIZER
            = SERIALIZERS.register("dissolver", () -> new DissolverRecipeSerializer<>(DissolverRecipe::new));

    public static final RegistryObject<EvaporatorRecipeSerializer<EvaporatorRecipe>> EVAPORATOR_SERIALIZER
            = SERIALIZERS.register("evaporator", () -> new EvaporatorRecipeSerializer<>(EvaporatorRecipe::new));

    public static final RegistryObject<FissionRecipeSerializer<FissionRecipe>> FISSION_SERIALIZER
            = SERIALIZERS.register("fission", () -> new FissionRecipeSerializer<>(FissionRecipe::new));

    public static final RegistryObject<LiquifierRecipeSerializer<LiquifierRecipe>> LIQUIFIER_SERIALIZER
            = SERIALIZERS.register("liquifier", () -> new LiquifierRecipeSerializer<>(LiquifierRecipe::new));

    private static final Map<RecipeType<? extends ProcessingRecipe>, List<? extends ProcessingRecipe>> recipesMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends ProcessingRecipe> List<T> getRecipesByType(RecipeType<T> pRecipeType, Level pLevel) {
        if (recipesMap.get(pRecipeType) == null) {
            List<T> recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType().equals(pRecipeType))
                    .map(recipe -> (T) recipe)
                    .collect(Collectors.toList());
            recipesMap.put(pRecipeType, recipes);
        }
        return (List<T>) recipesMap.get(pRecipeType);
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
