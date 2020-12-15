package al132.alchemistry.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientStack {


    public final Ingredient ingredient;
    public final int count;

    public IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public IngredientStack(Ingredient ingredient) {
        this(ingredient, 1);
    }

    public void write(PacketBuffer buf) {
        ingredient.write(buf);
        buf.writeInt(count);
    }

    public static IngredientStack read(PacketBuffer buf) {
        Ingredient ing = Ingredient.read(buf);
        int count = buf.readInt();
        return new IngredientStack(ing, count);
    }

    public List<ItemStack> toStacks() {
        return Arrays.stream(ingredient.getMatchingStacks())
                .map(x -> {
                    x.setCount(this.count);
                    return x;
                }).collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return "ingredient=" + ingredient + "\tcount=" + count;
    }
}
