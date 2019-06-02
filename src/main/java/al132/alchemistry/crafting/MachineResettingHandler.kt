package al132.alchemistry.crafting

import al132.alchemistry.Reference
import al132.alchemistry.blocks.ModBlocks
import al132.alib.utils.extensions.toStack
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class MachineResettingHandler : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    val machineStacks = listOf(
            ModBlocks.atomizer.toStack(),
            ModBlocks.chemical_combiner.toStack(),
            ModBlocks.chemical_dissolver.toStack(),
            ModBlocks.electrolyzer.toStack(),
            ModBlocks.evaporator.toStack(),
            ModBlocks.fissionController.toStack(),
            ModBlocks.fusionController.toStack(),
            ModBlocks.liquifier.toStack())

    init {
        this.setRegistryName(Reference.MODID, "machine_resetting_handler")
    }

    private var resultItem = ItemStack.EMPTY


    override fun canFit(width: Int, height: Int): Boolean = width * height >= 4

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun getCraftingResult(inv: InventoryCrafting) = resultItem.copy()

    override fun isDynamic(): Boolean = true

    override fun matches(inv: InventoryCrafting?, world: World?): Boolean {
        if (world == null || inv == null) return false
        var machine = ItemStack.EMPTY
        var emptySlots = 0

        for (i in 0 until inv.sizeInventory) {
            val currentStack = inv.getStackInSlot(i)
            if (!currentStack.isEmpty) {
                if (machine.isEmpty) {
                    machine = machineStacks.firstOrNull { currentStack.item == it.item } ?: ItemStack.EMPTY
                }
            } else emptySlots++
        }
        if (!machine.isEmpty && emptySlots == (inv.sizeInventory - 1)) {
            resultItem = machine.item.toStack()
            return true
        } else return false
    }
}