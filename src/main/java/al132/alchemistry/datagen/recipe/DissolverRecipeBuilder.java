package al132.alchemistry.datagen.recipe;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Ref;
import al132.alchemistry.datagen.DatagenUtils;
import al132.alchemistry.misc.ProbabilitySet;
import al132.alchemistry.utils.IngredientStack;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;

import static al132.alchemistry.Alchemistry.MODID;

public class DissolverRecipeBuilder extends BaseRecipeBuilder {


    private final String group = "minecraft:misc";
    private final String name;
    private final IngredientStack input;
    private final ProbabilitySet output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

    public DissolverRecipeBuilder(IngredientStack input, ProbabilitySet output, String name) {
        this.input = input;
        this.output = output;
        this.name = name;
    }

    public static DissolverRecipeBuilder recipe(IngredientStack input, ProbabilitySet output, String name) {
        return new DissolverRecipeBuilder(input, output, name);
    }

    public static DissolverRecipeBuilder recipe(Ingredient input, int count, ProbabilitySet output, String name) {
        return recipe(new IngredientStack(input, count), output, name);
    }

    public static DissolverRecipeBuilder recipe(Ingredient input, ProbabilitySet output, String name) {
        return recipe(new IngredientStack(input, 1), output, name);
    }


    public void build(Consumer<IFinishedRecipe> consumer) {
        this.build(consumer, new ResourceLocation(MODID, "dissolver/" + name));
    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                .withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(new DissolverRecipeBuilder.Result
                (id, this.group == null ? "" : this.group, this.input, this.output,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + "dissolver" + "/" + id.getPath())));

    }

    @Override
    void validate(ResourceLocation id) {

    }

    public static class Result implements IFinishedRecipe {
        private final String group;
        private final ResourceLocation id;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementID;
        private final IngredientStack input;
        private final ProbabilitySet output;

        public Result(ResourceLocation id, String group, IngredientStack input, ProbabilitySet output,
                      Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.output = output;
            this.advancementBuilder = advancementBuilder;
            this.advancementID = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            json.add("input", input.ingredient.serialize());
            json.add("output", output.serialize());
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return Ref.DISSOLVER_SERIALIZER;
        }

        @Override
        public JsonObject getAdvancementJson() {
            return this.advancementBuilder.serialize();
        }

        @Override
        public ResourceLocation getAdvancementID() {
            return this.advancementID;
        }
    }
}
