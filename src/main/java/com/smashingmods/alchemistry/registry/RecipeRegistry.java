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
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class RecipeRegistry {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static RegistryObject<RecipeType<AtomizerRecipe>> ATOMIZER_TYPE = registerRecipeType("atomizer");
    public static RegistryObject<RecipeType<CompactorRecipe>> COMPACTOR_TYPE = registerRecipeType("compactor");
    public static RegistryObject<RecipeType<CombinerRecipe>> COMBINER_TYPE = registerRecipeType("combiner");
    public static RegistryObject<RecipeType<DissolverRecipe>> DISSOLVER_TYPE = registerRecipeType("dissolver");
    public static RegistryObject<RecipeType<FissionRecipe>> FISSION_TYPE = registerRecipeType("fission");
    public static RegistryObject<RecipeType<FusionRecipe>> FUSION_TYPE = registerRecipeType("fusion");
    public static RegistryObject<RecipeType<LiquifierRecipe>> LIQUIFIER_TYPE = registerRecipeType("liquifier");

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

    private static final Map<RecipeType<? extends AbstractProcessingRecipe>, LinkedList<? extends AbstractProcessingRecipe>> recipeTypeMap = new LinkedHashMap<>();
    private static final Map<String, LinkedList<? extends AbstractProcessingRecipe>> recipeGroupMap = new LinkedHashMap<>();

    private static <T extends AbstractProcessingRecipe> RegistryObject<RecipeType<T>> registerRecipeType(String pType) {
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return pType;
            }
        };
        return RECIPE_TYPES.register(pType, () -> type);
    }

    /**
     * Attach a ReloadListener that clears the internal {@link RecipeRegistry#recipeTypeMap recipeTypeMap} and
     * {@link RecipeRegistry#recipeGroupMap recipeGroupMap} so that data pack reloading takes effect immediately.
     * @implNote This event handler just clears the internal maps, but a better version might update them in-place.
     *           That said, datapack reloads don't actually occur that often in regular play, so there is little point
     *           over-engineering this.
     * @param event the AddReloadListener event.
     */
    public static void postReload(final AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Boolean>() {
            @Override
            public String getName() {
                return "Alchemistry Cache Invalidator";
            }

            // Runs in the thread pool, figures out whether anything in non-standard Packs changed.
            @Override
            protected Boolean prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                // Always clear the maps on reload.
                return true;
            }

            // Runs on main thread; does the actual cache invalidation.
            @Override
            protected void apply(Boolean pShouldClear, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                if (pShouldClear) {
                    recipeTypeMap.clear();
                    recipeGroupMap.clear();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <R extends AbstractProcessingRecipe> LinkedList<R> getRecipesByType(RecipeType<R> pRecipeType, Level pLevel) {
        if (recipeTypeMap.get(pRecipeType) == null) {
            LinkedList<R> recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType().equals(pRecipeType))
                    .map(recipe -> (R) recipe)
                    .sorted()
                    .collect(Collectors.toCollection(LinkedList::new));
            recipeTypeMap.put(pRecipeType, recipes);
        }
        return (LinkedList<R>) recipeTypeMap.get(pRecipeType);
    }

    @SuppressWarnings("unchecked")
    public static <R extends AbstractProcessingRecipe> LinkedList<R> getRecipesByGroup(String pGroup, Level pLevel) {
        if (recipeGroupMap.get(pGroup) == null) {
            LinkedList<R> recipes = pLevel.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getGroup().equals(pGroup))
                .map(recipe -> (R) recipe)
                .sorted()
                .collect(Collectors.toCollection(LinkedList::new));
            recipeGroupMap.put(pGroup, recipes);
        }
        return (LinkedList<R>) recipeGroupMap.get(pGroup);
    }

    @SuppressWarnings("unchecked")
    public static <R extends AbstractProcessingRecipe> Optional<R> getRecipeByGroupAndId(String pGroup, ResourceLocation pRecipeId, Level pLevel) {
        return getRecipesByGroup(pGroup, pLevel).stream().filter(recipe -> recipe.getId().equals(pRecipeId)).findFirst().map(recipe -> (R) recipe);
    }

    public static LinkedList<AtomizerRecipe> getAtomizerRecipes(Level pLevel) {
        return getRecipesByType(ATOMIZER_TYPE.get(), pLevel);
    }

    public static LinkedList<CombinerRecipe> getCombinerRecipes(Level pLevel) {
        return getRecipesByType(COMBINER_TYPE.get(), pLevel);
    }

    public static LinkedList<CompactorRecipe> getCompactorRecipes(Level pLevel) {
        return getRecipesByType(COMPACTOR_TYPE.get(), pLevel);
    }

    public static LinkedList<DissolverRecipe> getDissolverRecipes(Level pLevel) {
        return getRecipesByType(DISSOLVER_TYPE.get(), pLevel);
    }

    public static LinkedList<FissionRecipe> getFissionRecipes(Level pLevel) {
        return getRecipesByType(FISSION_TYPE.get(), pLevel);
    }

    public static LinkedList<FusionRecipe> getFusionRecipes(Level pLevel) {
        return getRecipesByType(FUSION_TYPE.get(), pLevel);
    }

    public static LinkedList<LiquifierRecipe> getLiquifierRecipes(Level pLevel) {
        return getRecipesByType(LIQUIFIER_TYPE.get(), pLevel);
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
        RECIPE_TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}
