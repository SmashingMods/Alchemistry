package al132.alchemistry.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public abstract class AlchemistryRecipe implements IRecipe<IInventory> {

    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final IRecipeType<?> type;
    private final IRecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final String group;

    public AlchemistryRecipe(IRecipeType<?> type, IRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient ingredient, ItemStack result) {
        this.type = type;
        this.serializer = serializer;
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
    }


    public IRecipeType<?> getType() {
        return this.type;
    }

    public IRecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    public ResourceLocation getId() {
        return this.id;
    }

}
