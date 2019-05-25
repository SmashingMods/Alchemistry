package al132.alchemistry.crafting

import al132.alchemistry.Reference
import al132.alchemistry.items.ItemCompound
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class DankFoodHandler : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        this.setRegistryName(Reference.MODID, "dank_food_handler")
    }

    private var resultItem = ItemStack.EMPTY


    override fun canFit(width: Int, height: Int): Boolean = width * height >= 2

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun getCraftingResult(inv: InventoryCrafting) = resultItem.copy()

    override fun isDynamic(): Boolean = true

    override fun matches(inv: InventoryCrafting?, world: World?): Boolean {
        if(world == null || inv == null) return false
        var food = ItemStack.EMPTY
        var compound = ItemStack.EMPTY

        for (i in 0 until inv.sizeInventory) {
            val currentStack = inv.getStackInSlot(i)
            if (!currentStack.isEmpty) {
                if (currentStack.item is ItemFood) food = currentStack
                else {
                    if (currentStack.item !is ItemCompound || !ItemCompound.metaHasDankMolecule(currentStack.metadata)) {
                        return false
                    }
                    compound = currentStack
                }
            }
        }
        if (!food.isEmpty && !compound.isEmpty) {
            val tempResult: ItemStack = food.copy()//
            tempResult.count = 1
            if (tempResult.hasTagCompound()) {
                tempResult.tagCompound!!.apply {
                    this.setInteger("alchemistryPotion", compound.metadata)
                    this.setBoolean("alchemistrySalted", false)
                }
            } else tempResult.tagCompound = NBTTagCompound().apply {
                setInteger("alchemistryPotion", compound.metadata)
                setBoolean("alchemistrySalted", false)
            }
            resultItem = tempResult
            return true
        } else return false
    }
}