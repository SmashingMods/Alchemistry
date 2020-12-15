package al132.alchemistry.datagen.recipe;

import al132.alchemistry.Ref;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

import static al132.alchemistry.Alchemistry.MODID;

public class FissionRecipeBuilder extends BaseRecipeBuilder {
    private int input;
    private String group = "minecraft:misc";

    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

    public static FissionRecipeBuilder recipe(int input) {
        return new FissionRecipeBuilder(input);
    }

    public FissionRecipeBuilder(int inputNumber) {
        this.input = inputNumber;
    }

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        String name = Integer.toString(input);
        this.build(consumerIn, new ResourceLocation(MODID, "fission/" + name));

    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                .withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(new FissionRecipeBuilder.Result
                (id, this.group == null ? "" : this.group, this.input,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + this.input + "/" + id.getPath())));


    }

    @Override
    void validate(ResourceLocation id) {

    }

    public static class Result implements IFinishedRecipe {
        private final String group;
        private final ResourceLocation id;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementID;
        private final int input;

        public Result(ResourceLocation id, String group, int input, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.advancementBuilder = advancementBuilder;
            this.advancementID = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            json.addProperty("input", input);
            if (input % 2 == 0) {
                json.addProperty("output", input / 2);
                json.addProperty("output2", input / 2);
            } else {
                json.addProperty("output", (input / 2) + 1);
                json.addProperty("output2", input / 2);
            }

        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return Ref.FISSION_SERIALIZER;
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
