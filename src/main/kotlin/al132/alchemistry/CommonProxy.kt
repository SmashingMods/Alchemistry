package al132.alchemistry

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.client.GuiHandler
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.recipes.ModRecipes
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry

open class CommonProxy {


    open fun preInit(e: FMLPreInitializationEvent) {
        ConfigHandler.init(e.suggestedConfigurationFile)
        Reference.configPath = e.suggestedConfigurationFile.parent
        Reference.configDir = e.modConfigurationDirectory
        ElementRegistry.init()
        CompoundRegistry.init()
        //ModFluids.registerFluidContainers()
        PacketHandler.registerMessages(Reference.MODID)
    }

    open fun init(e: FMLInitializationEvent) {
       // if (Loader.isModLoaded("crafttweaker")) CTPlugin.init()
        NetworkRegistry.INSTANCE.registerGuiHandler(Alchemistry, GuiHandler())
    }

    open fun postInit(e: FMLPostInitializationEvent) {
        ModRecipes.init()
        //RecipeLoader.init()
    }

    //open fun initFluidModel(block: Block, fluid: Fluid) {}
}