package com.smashingmods.alchemistry.common.recipe.fission;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FissionRecipeSerializer<T extends FissionRecipe> implements RecipeSerializer<T> {

    private final FissionRecipeSerializer.IFactory<T> factory;
    private final Codec<T> codec;

    public FissionRecipeSerializer(FissionRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(FissionRecipe::getId),
                Codec.STRING.fieldOf("group").forGetter(FissionRecipe::getGroup),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("input").forGetter(FissionRecipe::getInput),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output1").forGetter(FissionRecipe::getOutput1),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output2").forGetter(FissionRecipe::getOutput2)
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
        ItemStack input = pBuffer.readItem();
        ItemStack output1 = pBuffer.readItem();
        ItemStack output2 = pBuffer.readItem();
        return factory.create(id, group, input, output1, output2);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeItem(pRecipe.getInput());
        pBuffer.writeItem(pRecipe.getOutput1());
        pBuffer.writeItem(pRecipe.getOutput2());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, ItemStack pInput, ItemStack pOutput1, ItemStack pOutput2);
    }
}
