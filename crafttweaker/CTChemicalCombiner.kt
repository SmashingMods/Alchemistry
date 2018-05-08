package al132.alchemistry.compat.crafttweaker

import al132.alchemistry.recipes.CombinerRecipe
import minetweaker.IUndoableAction
import minetweaker.MineTweakerAPI
import minetweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.Optional
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

/**
 * Created by al132 on 4/28/2017.
 */

//TODO
@ZenClass("mods.alchemistry.ChemicalCombiner")
class CTChemicalCombiner {
    val name = "Alchemistry Chemical Combiner"

    @ZenMethod
    fun addRecipe(output: IItemStack, input1: IItemStack, @Optional input2: IItemStack, @Optional input3: IItemStack,
                  @Optional input4: IItemStack, @Optional input5: IItemStack, @Optional input6: IItemStack,
                  @Optional input7: IItemStack, @Optional input8: IItemStack, @Optional input9: IItemStack) {

        val inputStacks = ArrayList<ItemStack>()
        listOf(input1, input2, input3, input4, input5, input6, input7, input8, input9).forEach { inputStacks.add(it.toStack()) }
        MineTweakerAPI.apply(Add(CombinerRecipe(output.toStack(), inputStacks)))
    }

    @ZenMethod
    fun remove() {
    }


    private class Add(recipe: CombinerRecipe) : IUndoableAction {

        override fun getOverrideKey(): Any? = null

        override fun describe() = ""

        override fun apply() {}

        override fun undo() {}

        override fun canUndo() = true

        override fun describeUndo() = ""
    }


    private class Remove : IUndoableAction {

        override fun getOverrideKey(): Any? = null

        override fun describe() = ""

        override fun apply() {}

        override fun undo() {}

        override fun canUndo() = true

        override fun describeUndo() = ""
    }
}