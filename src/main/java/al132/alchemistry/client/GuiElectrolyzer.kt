package al132.alchemistry.client

import al132.alchemistry.tiles.TileElectrolyzer
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer)
    : GuiBase<TileElectrolyzer>(ContainerElectrolyzer(playerInv, tile), tile, GuiElectrolyzer.textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "electrolyzer_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.electrolyzer.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 68, 16, 60, tile::energyStorage))
        this.displayData.add(CapabilityFluidDisplayWrapper(48, 40, 16, 60, tile::inputTank))
    }
}