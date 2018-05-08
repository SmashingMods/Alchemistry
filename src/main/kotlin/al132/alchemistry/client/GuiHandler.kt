package al132.alchemistry.client

import al132.alchemistry.tiles.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler


class GuiHandler : IGuiHandler {

    companion object {
        val NO_GUI = -1
        val ELECTROLYZER_ID = 0
        val CHEMICAL_DISSOLVER_ID = 1
        val CHEMICAL_COMBINER_ID = 2
        val EVAPORATOR_ID = 3
        val ALLOY_FURNACE_ID = 4
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val entity = world.getTileEntity(BlockPos(x, y, z))
        when (ID) {
            ELECTROLYZER_ID -> {
                if (entity is TileElectrolyzer) return ContainerElectrolyzer(player.inventory, entity)
            }
            CHEMICAL_DISSOLVER_ID -> {
                if (entity is TileChemicalDissolver) return ContainerChemicalDissolver(player.inventory, entity)
            }
            CHEMICAL_COMBINER_ID -> {
                if (entity is TileChemicalCombiner) return ContainerChemicalCombiner(player.inventory, entity)
            }
            EVAPORATOR_ID -> {
                if (entity is TileEvaporator) return ContainerEvaporator(player.inventory, entity)
            }
            ALLOY_FURNACE_ID -> {
                if (entity is TileAlloyFurnace) return ContainerAlloyFurnace(player.inventory, entity)
            }
        }
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val entity = world.getTileEntity(BlockPos(x, y, z))
        when (ID) {
            ELECTROLYZER_ID -> {
                if (entity is TileElectrolyzer) return GuiElectrolyzer(player.inventory, entity)
            }
            CHEMICAL_DISSOLVER_ID -> {
                if (entity is TileChemicalDissolver) return GuiChemicalDissolver(player.inventory, entity)
            }
            CHEMICAL_COMBINER_ID -> {
                if (entity is TileChemicalCombiner) return GuiChemicalCombiner(player.inventory, entity)
            }
            EVAPORATOR_ID -> {
                if (entity is TileEvaporator) return GuiEvaporator(player.inventory, entity)
            }
            ALLOY_FURNACE_ID -> {
                if (entity is TileAlloyFurnace) return GuiAlloyFurnace(player.inventory, entity)
            }
        }
        return null
    }
}