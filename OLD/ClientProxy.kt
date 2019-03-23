package al132.alchemistry.utils


import al132.alchemistry.items.ModItems
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.common.event.FMLInitializationEvent
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


    /*override fun initFluidModel(block: Block, fluid: Fluid) {
        val fluidLocation = ModelResourceLocation(
                "alchemistry:textures/blocks" + block.unlocalizedName)

        // use a custom state mapper which will ignore the LEVEL property
        ModelLoader.setCustomStateMapper(block, object : StateMapperBase() {
            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                return fluidLocation
            }
        })
    }*/
}