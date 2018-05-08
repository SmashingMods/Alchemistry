package al132.alchemistry.compat.crafttweaker.utils;

import minetweaker.api.entity.IEntity;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.mc1112.item.MCItemStack;
import minetweaker.mc1112.liquid.MCLiquidStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Array;
import java.util.ArrayList;

//From MTLib, MIT License
public class InputHelper {
    public static boolean isABlock(IItemStack block) {
        if (!(isABlock(toStack(block)))) {
            return false;
        } else
            return true;
    }

    public static <T> T[][] getMultiDimensionalArray(Class<T> clazz, T[] array, int height, int width) {

        @SuppressWarnings("unchecked")
        T[][] multiDim = (T[][]) Array.newInstance(clazz, new int[]{height, width});

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                multiDim[y][x] = array[x + (y * width)];
            }
        }

        return multiDim;
    }

    public static IItemStack[] toStacks(IIngredient[] iIngredient) {
        ArrayList<IItemStack> stacks = new ArrayList<IItemStack>();

        for (IIngredient ing : iIngredient) {
            for (IItemStack stack : ing.getItems()) {
                stacks.add(stack);
            }
        }

        return stacks.toArray(new IItemStack[stacks.size()]);
    }

    public static boolean isABlock(ItemStack block) {
        return block.getItem() instanceof ItemBlock;
    }

    public static Entity toEntity(IEntity iEntity) {
        return null;
    }

    public static ItemStack toStack(IItemStack iStack) {
        if (iStack == null) {
            return null;
        } else {
            Object internal = iStack.getInternal();
            if (!(internal instanceof ItemStack)) {
                //LogHelper.logError("Not a valid item stack: " + iStack);
            }

            return (ItemStack) internal;
        }
    }

    public static IIngredient toIngredient(ItemStack stack) {
        return toIItemStack(stack);
    }

    public static IIngredient toIngredient(FluidStack stack) {
        if (stack == null) {
            return null;
        }

        return new MCLiquidStack(stack);
    }

    public static IItemStack toIItemStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        return new MCItemStack(stack);
    }

    public static ILiquidStack toILiquidStack(FluidStack stack) {
        if (stack == null) {
            return null;
        }

        return new MCLiquidStack(stack);
    }

    public static ItemStack[] toStacks(IItemStack[] iStack) {
        if (iStack == null) {
            return null;
        } else {
            ItemStack[] output = new ItemStack[iStack.length];
            for (int i = 0; i < iStack.length; i++) {
                output[i] = toStack(iStack[i]);
            }

            return output;
        }
    }

    public static Object toObject(IIngredient iStack) {
        if (iStack == null)
            return null;
        else {
            if (iStack instanceof IOreDictEntry) {
                return toString((IOreDictEntry) iStack);
            } else if (iStack instanceof IItemStack) {
                return toStack((IItemStack) iStack);
            } else
                return null;
        }
    }

    public static Object[] toObjects(IIngredient[] ingredient) {
        if (ingredient == null)
            return null;
        else {
            Object[] output = new Object[ingredient.length];
            for (int i = 0; i < ingredient.length; i++) {
                if (ingredient[i] != null) {
                    output[i] = toObject(ingredient[i]);
                } else
                    output[i] = "";
            }

            return output;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object[] toShapedObjects(IIngredient[][] ingredients) {
        if (ingredients == null)
            return null;
        else {
            ArrayList prep = new ArrayList();
            prep.add("abc");
            prep.add("def");
            prep.add("ghi");
            char[][] map = new char[][]{{'a', 'b', 'c'}, {'d', 'e', 'f'}, {'g', 'h', 'i'}};
            for (int x = 0; x < ingredients.length; x++) {
                if (ingredients[x] != null) {
                    for (int y = 0; y < ingredients[x].length; y++) {
                        if (ingredients[x][y] != null && x < map.length && y < map[x].length) {
                            prep.add(map[x][y]);
                            prep.add(toObject(ingredients[x][y]));
                        }
                    }
                }
            }
            return prep.toArray();
        }
    }

    public static String toString(IOreDictEntry entry) {
        return ((IOreDictEntry) entry).getName();
    }

    public static FluidStack toFluid(ILiquidStack iStack) {
        if (iStack == null) {
            return null;
        } else
            return FluidRegistry.getFluidStack(iStack.getName(), iStack.getAmount());
    }

    public static Fluid getFluid(ILiquidStack iStack) {
        if (iStack == null) {
            return null;
        } else
            return FluidRegistry.getFluid(iStack.getName());

    }

    public static FluidStack[] toFluids(IIngredient[] input) {
        return toFluids((IItemStack[]) input);
    }

    public static FluidStack[] toFluids(ILiquidStack[] iStack) {
        FluidStack[] stack = new FluidStack[iStack.length];
        for (int i = 0; i < stack.length; i++)
            stack[i] = toFluid(iStack[i]);
        return stack;
    }
}