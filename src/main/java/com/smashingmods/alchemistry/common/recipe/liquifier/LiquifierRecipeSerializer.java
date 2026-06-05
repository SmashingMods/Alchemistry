package com.smashingmods.alchemistry.common.recipe.liquifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class LiquifierRecipeSerializer<T extends LiquifierRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final Codec<T> codec;

    public LiquifierRecipeSerializer(IFactory<T> factory) {
        this.factory = factory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(LiquifierRecipe::getId),
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "liquifier").forGetter(LiquifierRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.fieldOf("input").forGetter(LiquifierRecipe::getInput),
                AlchemistryRecipeCodecs.FLUID_STACK.fieldOf("result").forGetter(LiquifierRecipe::getOutput)
        ).apply(instance, factory::create));
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
        FluidStack output = pBuffer.readFluidStack();
        return this.factory.create(id, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pRecipe.getInput().toNetwork(pBuffer);
        pBuffer.writeFluidStack(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, IngredientStack pInput, FluidStack pOutput);
    }
}
