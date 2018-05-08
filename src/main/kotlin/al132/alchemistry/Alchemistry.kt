package al132.alchemistry

import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.items.ModItems
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

//TODO: Everything
@Mod(modid = Reference.MODID,
        name = Reference.MODNAME,
        version = Reference.VERSION,
        dependencies = Reference.DEPENDENCIES,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Alchemistry {

    @SidedProxy(clientSide = "al132.alchemistry.ClientProxy", serverSide = "al132.alchemistry.CommonProxy")
    var proxy: CommonProxy? = null

    @EventHandler
    fun preInit(e: FMLPreInitializationEvent) = proxy!!.preInit(e)

    @EventHandler
    fun init(e: FMLInitializationEvent) = proxy!!.init(e)

    @EventHandler
    fun postInit(e: FMLPostInitializationEvent) = proxy!!.postInit(e)

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    object Registration {

        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            ModBlocks.registerBlocks(event)
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            ModBlocks.registerItemBlocks(event)
            ModItems.registerItems(event)
        }

        @SideOnly(Side.CLIENT)
        @JvmStatic
        @SubscribeEvent
        fun registerModels(event: ModelRegistryEvent) {
            ModBlocks.registerModels()
            ModItems.registerModels()
        }
    }
}