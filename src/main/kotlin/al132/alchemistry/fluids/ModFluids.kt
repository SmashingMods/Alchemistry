package al132.alchemistry.fluids

import al132.alchemistry.Reference
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

//Courtesy of Choonster, (MIT License) https://github.com/Choonster/TestMod3
object ModFluids {
    /**
     * The fluids registered by this mod. Includes fluids that were already registered by another mod.
     */
    val FLUIDS: MutableSet<Fluid> = HashSet()

    /**
     * The fluid blocks from this mod only. Doesn't include blocks for fluids that were already registered by another mod.
     */
    val MOD_FLUID_BLOCKS: MutableSet<IFluidBlock> = HashSet()

    /*
    public static final Fluid  HYDROCHLORIC_ACID = createFluid("hydrochloric_acid", true,
			fluid -> fluid.setDensity(1600).setViscosity(2000).canBePlacedInWorld(),
			fluid -> new BlockFluidClassic(fluid, new MaterialLiquid(MapColor.ADOBE)));
	*/


    /**
     * Create a [Fluid] and its [IFluidBlock], or use the existing ones if a fluid has already been registered with the same name.

     * @param name                 The name of the fluid
     * *
     * @param hasFlowIcon          Does the fluid have a flow icon?
     * *
     * @param fluidPropertyApplier A function that sets the properties of the [Fluid]
     * *
     * @param blockFactory         A function that creates the [IFluidBlock]
     * *
     * @return The fluid and block
     */
    private fun <T> createFluid(name: String,
                                hasFlowIcon: Boolean,
                                fluidPropertyApplier: Consumer<Fluid>,
                                blockFactory: Function<Fluid, T>):
            Fluid where T : Block, T : IFluidBlock {

        val texturePrefix = Reference.pathPrefix + "fluids/"

        val still = ResourceLocation(texturePrefix + name + "_still")
        val flowing = if (hasFlowIcon) ResourceLocation(texturePrefix + name + "_flow") else still

        var fluid = Fluid(name, still, flowing)
        val useOwnFluid = FluidRegistry.registerFluid(fluid)

        if (useOwnFluid) {
            fluidPropertyApplier.accept(fluid)
            MOD_FLUID_BLOCKS.add(blockFactory.apply(fluid))
        } else {
            fluid = FluidRegistry.getFluid(name)
        }

        FLUIDS.add(fluid)

        return fluid
    }

    @Mod.EventBusSubscriber
    object RegistrationHandler {

        /**
         * Register this mod's fluid [Block]s.

         * @param event The event
         */
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            val registry = event.registry

            for (fluidBlock in MOD_FLUID_BLOCKS) {
                val block = fluidBlock as Block
                block.setRegistryName(Reference.MODID, "fluid." + fluidBlock.fluid.name)
                block.unlocalizedName = Reference.pathPrefix + fluidBlock.fluid.unlocalizedName
                block.setCreativeTab(Reference.creativeTab)
                registry.register(block)
            }
        }

        /**
         * Register this mod's fluid [ItemBlock]s.

         * @param event The event
         */
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            val registry = event.registry

            for (fluidBlock in MOD_FLUID_BLOCKS) {
                val block = fluidBlock as Block
                val itemBlock = ItemBlock(block)
                itemBlock.registryName = block.registryName!!
                registry.register(itemBlock)
            }
        }
    }

    fun registerFluidContainers() {

        for (fluid in FLUIDS) {
            registerBucket(fluid)
        }
    }

    private fun registerBucket(fluid: Fluid) {
        FluidRegistry.addBucketForFluid(fluid)
    }
}