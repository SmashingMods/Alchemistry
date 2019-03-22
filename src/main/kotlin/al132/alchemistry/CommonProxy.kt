package al132.alchemistry

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.client.GuiHandler
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.XMLRecipeParser
import crafttweaker.CraftTweakerAPI
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.io.File
import java.io.FileFilter

open class CommonProxy {

    open fun preInit(e: FMLPreInitializationEvent) {
        Alchemistry.logger = e.modLog
        ConfigHandler.init(e.suggestedConfigurationFile)
        Reference.configPath = e.suggestedConfigurationFile.parent
        Reference.configDir = File(e.modConfigurationDirectory, "alchemistry")
        if (!Reference.configDir.exists()) Reference.configDir.mkdir()
        val exampleFile = File(Reference.configDir, "custom.xml")
        if (!exampleFile.exists()) {
            exampleFile.printWriter().use { out ->
                out.println("<!--Read the wiki for more info on using custom recipes https://github.com/al132mc/alchemistry/wiki -->")
                out.println("<recipes>")
                out.println("</recipes>")
            }
        }
        ElementRegistry.init()
        CompoundRegistry.init()

        PacketHandler.registerMessages(Reference.MODID)

        CraftTweakerAPI.tweaker.loadScript(false, "alchemistry")
    }

    open fun init(e: FMLInitializationEvent) {
        val files = Reference.configDir.listFiles(FileFilter { it.extension.toLowerCase() == "xml" })
        files.forEach {
            XMLRecipeParser().init(it.name)
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(Alchemistry, GuiHandler())
        MinecraftForge.EVENT_BUS.register(EventHandler())
    }

    open fun postInit(e: FMLPostInitializationEvent) {
        ModRecipes.init()
    }
}