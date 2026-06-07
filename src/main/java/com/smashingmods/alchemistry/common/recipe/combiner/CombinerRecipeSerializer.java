package com.smashingmods.alchemistry.common.recipe.combiner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashSet;
import java.util.Set;

public class CombinerRecipeSerializer<T extends CombinerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final Codec<T> codec;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CombinerRecipe::getId),
                Codec.STRING.fieldOf("group").forGetter(CombinerRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.listOf().fieldOf("input").forGetter(CombinerRecipe::getInput),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(CombinerRecipe::getOutput)
        ).apply(instance, (id, group, input, output) -> pFactory.create(id, group, new LinkedHashSet<>(input), output)));
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf pBuffer) {
        ResourceLocation id = pBuffer.readResourceLocation();
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        int inputCount = pBuffer.readInt();
        Set<IngredientStack> inputList = new LinkedHashSet<>();
        for (int i = 0; i < inputCount; i++) {
            inputList.add(IngredientStack.fromNetwork(pBuffer));
        }
        ItemStack output = pBuffer.readItem();
        return this.factory.create(id, group, inputList, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeInt(pRecipe.getInput().size());
        for (int i = 0; i < pRecipe.getInput().size(); i++) {
            pRecipe.getInput().get(i).toNetwork(pBuffer);
        }
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, Set<IngredientStack> pInput, ItemStack pOutput);
    }
}
