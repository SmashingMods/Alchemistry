package al132.alchemistry

import al132.alchemistry.capability.AlchemistryDrugInfo
import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.client.GuiHandler
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.XMLRecipeParser
import crafttweaker.CraftTweakerAPI
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.Loader
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
        registerCapabilities()
        SoundHandler.init()
        ElementRegistry.init()
        CompoundRegistry.init()
        PacketHandler.registerMessages(Reference.MODID)

        if (Loader.isModLoaded("crafttweaker")) CraftTweakerAPI.tweaker.loadScript(false, "alchemistry")
    }

    open fun init(e: FMLInitializationEvent) {
        ModRecipes.initOredict()
        Reference.configDir
                .listFiles(FileFilter { it.extension.toLowerCase() == "xml" })
                .forEach { XMLRecipeParser().init(it.name) }
        NetworkRegistry.INSTANCE.registerGuiHandler(Alchemistry, GuiHandler())
        MinecraftForge.EVENT_BUS.register(EventHandler())
    }

    open fun postInit(e: FMLPostInitializationEvent) {
        ModRecipes.init()
    }

    private fun registerCapabilities() {
        CapabilityManager.INSTANCE.register(AlchemistryDrugInfo::class.java, object : Capability.IStorage<AlchemistryDrugInfo> {

            override fun writeNBT(capability: Capability<AlchemistryDrugInfo>, instance: AlchemistryDrugInfo, side: EnumFacing): NBTBase? {
                throw UnsupportedOperationException()
            }

            override fun readNBT(capability: Capability<AlchemistryDrugInfo>, instance: AlchemistryDrugInfo, side: EnumFacing, nbt: NBTBase) {
                throw UnsupportedOperationException()
            }

        }) { throw UnsupportedOperationException() }
    }
}