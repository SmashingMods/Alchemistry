package al132.alchemistry.recipes;

import al132.alchemistry.Ref;
import al132.chemlib.chemistry.CompoundRegistry;
import al132.chemlib.chemistry.ElementRegistry;
import al132.chemlib.items.CompoundItem;
import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

import static al132.alchemistry.utils.StringUtils.toStack;

public class ModRecipes {

    public static List<DissolverRecipe> dissolverRecipes = new ArrayList<>();
    public static List<CombinerRecipe> combinerRecipes = new ArrayList<>();
    public static List<EvaporatorRecipe> evaporatorRecipes = new ArrayList<>();
    public static List<AtomizerRecipe> atomizerRecipes = new ArrayList<>();
    public static List<LiquifierRecipe> liquifierRecipes = new ArrayList<>();
    public static List<FissionRecipe> fissionRecipes = new ArrayList<>();

    public static void init() {
        initEvaporatorRecipes();
        initFuelHandler();
        initDissolverRecipes();//before combiner recipes, so combiner can use reversible recipes
        initCombinerRecipes();
        initAtomizerRecipes(); //before liquifier recipes, for reversible recipes
        initLiquifierRecipes();
        initFissionRecipes();
    }

    private static void initFuelHandler() {

    }

    private static void initEvaporatorRecipes() {
        evaporatorRecipes.add(new EvaporatorRecipe(new FluidStack(Fluids.WATER, 125), new ItemStack(Ref.mineralSalt)));
        evaporatorRecipes.add(new EvaporatorRecipe(new FluidStack(Fluids.LAVA, 1000), new ItemStack(Blocks.MAGMA_BLOCK)));
        //TODO milk
    }

    private static void initDissolverRecipes() {
        Item cellulose = ForgeRegistries.ITEMS.getValue(new ResourceLocation("chemlib", "compound_cellulose"));

        dissolver().input(Items.OAK_LOG)
                .outputs(set().addGroup(1.0, new ItemStack(cellulose)).build())
                .build();
        for (CompoundItem compound : CompoundRegistry.compounds) {
            dissolver().input(compound)
                    .outputs(set().addGroup(1.0, compound.getComponentStacks().toArray(new ItemStack[0])).build())
                    .build();
        }
    }

    private static void initCombinerRecipes() {
        combinerRecipes.add(
                new CombinerRecipe(new ItemStack(Items.CHARCOAL),
                        Lists.newArrayList(null, null, toStack("carbon", 8))));
        //"carbon".toStack(8))))

    }

    private static void initAtomizerRecipes() {
        atomizerRecipes.add(new AtomizerRecipe(true, new FluidStack(Fluids.WATER, 500), toStack("water", 8)));
    }

    private static void initLiquifierRecipes() {
        for (AtomizerRecipe recipe : atomizerRecipes) {
            liquifierRecipes.add(new LiquifierRecipe(recipe.output,recipe.input));
        }
    }

    private static void initFissionRecipes() {
        for (int i = 2; i <= 118; i++) {
            int output1 = (i % 2 == 0) ? i / 2 : (i / 2) + 1;
            int output2 = (i % 2 == 0) ? 0 : i / 2;
            if (ElementRegistry.elements.get(output1) != null && (output2 == 0 || ElementRegistry.elements.get(output2) != null)) {
                fissionRecipes.add(new FissionRecipe(i, output1, output2));
            }
        }
    }

    public static DissolverRecipe.Builder dissolver() {
        return new DissolverRecipe.Builder();
    }

    public static ProbabilitySet.Builder set() {
        return new ProbabilitySet.Builder();
    }
}
