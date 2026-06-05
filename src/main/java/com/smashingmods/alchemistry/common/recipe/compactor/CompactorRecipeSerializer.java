package com.smashingmods.alchemistry.common.recipe.compactor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import javax.annotation.Nullable;

public class CompactorRecipeSerializer<T extends CompactorRecipe> implements RecipeSerializer<T> {

    private final CompactorRecipeSerializer.IFactory<T> factory;
    private final Codec<T> codec;

    public CompactorRecipeSerializer(CompactorRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CompactorRecipe::getId),
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "compactor").forGetter(CompactorRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.fieldOf("input").forGetter(CompactorRecipe::getInput),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(CompactorRecipe::getOutput)
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
        IngredientStack input = IngredientStack.fromNetwork(pBuffer);
        ItemStack output = pBuffer.readItem();
        return this.factory.create(id, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeResourceLocation(pRecipe.getId());
        pBuffer.writeUtf(pRecipe.getGroup());
        pRecipe.getInput().toNetwork(pBuffer);
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, IngredientStack pInput, ItemStack pOutput);
    }
}
