package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DissolverRecipeSerializer<T extends DissolverRecipe> implements RecipeSerializer<T> {

    private final DissolverRecipeSerializer.IFactory<T> factory;
    private final Codec<T> codec;

    public DissolverRecipeSerializer(DissolverRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(DissolverRecipe::getId),
                Codec.STRING.fieldOf("group").forGetter(DissolverRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.fieldOf("input").forGetter(DissolverRecipe::getInput),
                ProbabilitySet.CODEC.fieldOf("output").forGetter(DissolverRecipe::getOutput)
        ).apply(instance, pFactory::create));
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf pBuffer) {
        ResourceLocation id = pBuffer.readResourceLocation();
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        IngredientStack input = IngredientStack.fromNetwork(pBuffer);
        ProbabilitySet output = ProbabilitySet.fromNetwork(pBuffer);
        return this.factory.create(id, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pRecipe.getInput().toNetwork(pBuffer);
        pRecipe.getOutput().toNetwork(pBuffer);
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, IngredientStack pInput, ProbabilitySet pOutput);
    }
}
