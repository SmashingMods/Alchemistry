package al132.alchemistry


import al132.alchemistry.items.ModItems
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ClientProxy : CommonProxy() {

    @SideOnly(Side.CLIENT)
    override fun preInit(e: FMLPreInitializationEvent) {
        super.preInit(e)
        OBJLoader.INSTANCE.addDomain(Reference.MODID)
    }

    override fun postInit(e: FMLPostInitializationEvent) {
        super.postInit(e)
        ModItems.initColors()
    }
}