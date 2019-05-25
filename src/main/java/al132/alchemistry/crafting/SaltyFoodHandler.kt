package al132.alchemistry.crafting

import al132.alchemistry.Reference
import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.items.ItemCompound
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class SaltyFoodHandler : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        this.setRegistryName(Reference.MODID, "salty_food_handler")
    }

    private var resultItem = ItemStack.EMPTY


    override fun canFit(width: Int, height: Int): Boolean = width * height >= 2

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun getCraftingResult(inv: InventoryCrafting) = resultItem.copy()

    override fun isDynamic(): Boolean = true

    override fun matches(inv: InventoryCrafting?, world: World?): Boolean {
        if(world == null || inv == null) return false
        var food = ItemStack.EMPTY
        var countSalt = 0

        for (i in 0 until inv.sizeInventory) {
            val currentStack = inv.getStackInSlot(i)
            if (!currentStack.isEmpty) {
                if (currentStack.item is ItemFood
                        && currentStack.hasTagCompound()
                        && currentStack.tagCompound?.hasKey("alchemistryPotion") ?: false) food = currentStack
                else {
                    if (currentStack.item is ItemCompound && currentStack.metadata == CompoundRegistry["sodium_chloride"]!!.meta) {
                        countSalt++
                    } else return false
                }
            }
        }
        if (!food.isEmpty && countSalt == 8) {
            val tempResult: ItemStack = food.copy()
            tempResult.count = 1
            tempResult.tagCompound!!.setBoolean("alchemistrySalted", true)
            resultItem = tempResult
            return true
        } else return false
    }
}