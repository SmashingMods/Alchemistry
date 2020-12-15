package al132.alchemistry.misc;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class ProcessingRecipe implements IRecipe<IInventory> {

    protected final IRecipeType<?> type;
    //private final IRecipeSerializer<?> serializer;
    protected final String group;
    protected final ItemStack output1;
    protected final Ingredient input1;
    public final ResourceLocation id;

    public ProcessingRecipe(IRecipeType<?> recipeType, ResourceLocation id, String group, Ingredient input, ItemStack output) {
        this.type = recipeType;
        this.id = id;
        this.group = group;
        this.output1 = output;
        this.input1 = input;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeType<?> getType() {
        return this.type;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output1;
    }


    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return this.output1.copy();
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return this.input1.test(inv.getStackInSlot(0));
    }
}