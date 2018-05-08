package al132.alchemistry.client

import al132.alchemistry.tiles.TileChemicalDissolver
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.IResource
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiChemicalDissolver(playerInv: InventoryPlayer, tile: TileChemicalDissolver) :
        GuiBase<TileChemicalDissolver>(ContainerChemicalDissolver(playerInv, tile), tile) {

    override val displayName = "Chemical Dissolver"

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(13, 31, 16, 60, tile::energyCapability))
    }

    companion object: IResource {
        override fun textureLocation() = ResourceLocation(GuiBase.root + "chemical_dissolver_gui.png")
    }
}