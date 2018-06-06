package al132.alchemistry.client

import al132.alchemistry.tiles.TileAtomizer
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import al132.alib.client.IResource
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiAtomizer(playerInv: InventoryPlayer, tile: TileAtomizer) :
        GuiBase<TileAtomizer>(ContainerAtomizer(playerInv, tile), tile) {

    override val displayName = "Atomizer"

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyCapability))
        this.displayData.add(CapabilityFluidDisplayWrapper(48, 40, 16, 60, tile::inputTank))
    }

    companion object: IResource {
        override fun textureLocation() = ResourceLocation(GuiBase.root + "atomizer_gui.png")
    }
}