package com.smashingmods.alchemistry.common.recipe.fusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import javax.annotation.Nullable;

public class FusionRecipeSerializer<T extends FusionRecipe> implements RecipeSerializer<T> {

    private final FusionRecipeSerializer.IFactory<T> factory;
    private final Codec<T> codec;

    public FusionRecipeSerializer(FusionRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(FusionRecipe::getId),
                Codec.STRING.fieldOf("group").forGetter(FusionRecipe::getGroup),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("input1").forGetter(FusionRecipe::getInput1),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("input2").forGetter(FusionRecipe::getInput2),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("output").forGetter(FusionRecipe::getOutput)
        ).apply(instance, pFactory::create));
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Nullable
    @Override
    public T fromNetwork(FriendlyByteBuf pBuffer) {
        ResourceLocation id = pBuffer.readResourceLocation();
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        ItemStack input1 = pBuffer.readItem();
        ItemStack input2 = pBuffer.readItem();
        ItemStack output = pBuffer.readItem();
        return factory.create(id, group, input1, input2, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeItem(pRecipe.getInput1());
        pBuffer.writeItem(pRecipe.getInput2());
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, ItemStack pInput1, ItemStack pInput2, ItemStack pOutput);
    }
}
