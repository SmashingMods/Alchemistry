package al132.alchemistry.datagen.recipe;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Ref;
import al132.alchemistry.datagen.DatagenUtils;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CombinerRecipeBuilder extends BaseRecipeBuilder {

    private final String group = "minecraft:misc";
    private final List<ItemStack> input;
    private final ItemStack output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

    public CombinerRecipeBuilder(List<ItemStack> input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public static CombinerRecipeBuilder recipe(ItemStack output, ItemStack... input) {
        return recipe(output, Arrays.asList(input));
    }

    public static CombinerRecipeBuilder recipe(ItemStack output, List<ItemStack> input) {
        return new CombinerRecipeBuilder(input, output);
    }


    public void build(Consumer<IFinishedRecipe> consumerIn) {
        String name = this.output.getItem().getRegistryName().getPath();
        this.build(consumerIn, new ResourceLocation(Alchemistry.MODID, "combiner/" + name));
    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                .withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(new CombinerRecipeBuilder.Result
                (id, this.group == null ? "" : this.group, this.input, this.output,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + this.output.getItem().getGroup().getPath() + "/" + id.getPath())));


    }

    @Override
    void validate(ResourceLocation id) {

    }

    public static class Result implements IFinishedRecipe {
        private final String group;
        private final ResourceLocation id;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementID;
        private final List<ItemStack> input;
        private final ItemStack output;

        public Result(ResourceLocation id, String group, List<ItemStack> input, ItemStack output,
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
            DatagenUtils.addStackListToJson(json, "input", input);
            DatagenUtils.addStackToJson(json, "result", output);
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return Ref.COMBINER_SERIALIZER;
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
