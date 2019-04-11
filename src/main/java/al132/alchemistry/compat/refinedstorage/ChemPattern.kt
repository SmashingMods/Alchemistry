package al132.alchemistry.compat.refinedstorage

import al132.alchemistry.utils.toStack
import al132.alib.utils.extensions.toStack
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack

class ChemPattern(private val world: World, private var _container: ICraftingPatternContainer?, private val _stack: ItemStack) : ICraftingPattern {


    private val inputs = NonNullList.create<ItemStack>()
    private val output = NonNullList.create<ItemStack>()

    init {
    }

    override fun getChainHashCode(): Int = 1 //TODO

    override fun getId(): String = CraftingTaskFactory.ID

    override fun getByproducts(): NonNullList<ItemStack> = NonNullList.create()

    override fun getByproducts(p0: NonNullList<ItemStack>?): NonNullList<ItemStack> = NonNullList.create()

    override fun getFluidInputs(): NonNullList<FluidStack> = NonNullList.create()

    override fun getStack(): ItemStack = _stack

    override fun getOutputs(): NonNullList<ItemStack> {
        return NonNullList.from(ItemStack(Items.COAL))
    }

    override fun getFluidOutputs(): NonNullList<FluidStack> = NonNullList.create()

    override fun getInputs(): MutableList<NonNullList<ItemStack>> {
        return mutableListOf(NonNullList.from("carbon".toStack(8)))
    }

    override fun canBeInChainWith(p0: ICraftingPattern?): Boolean = false //TODO

    override fun isProcessing(): Boolean = true

    override fun getOutput(p0: NonNullList<ItemStack>?): ItemStack {
        return Items.COAL.toStack()
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun getContainer(): ICraftingPatternContainer? = _container

    override fun isOredict(): Boolean = false //TODO

}