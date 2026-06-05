package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final Codec<T> codec;

    public AtomizerRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(AtomizerRecipe::getId),
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "atomizer").forGetter(AtomizerRecipe::getGroup),
                AlchemistryRecipeCodecs.FLUID_STACK.fieldOf("input").forGetter(AtomizerRecipe::getInput),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(AtomizerRecipe::getOutput)
        ).apply(instance, pFactory::create));
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf pBuffer) {
        ResourceLocation id = pBuffer.readResourceLocation();
        String recipeGroup = pBuffer.readUtf(Short.MAX_VALUE);
        FluidStack input = pBuffer.readFluidStack();
        ItemStack output = pBuffer.readItem();
        return this.factory.create(id, recipeGroup, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeFluidStack(pRecipe.getInput());
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}
