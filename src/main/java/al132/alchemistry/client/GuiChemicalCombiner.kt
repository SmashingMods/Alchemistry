package al132.alchemistry.client

import al132.alchemistry.network.ChemicalCombinerPacket
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.tiles.TileChemicalCombiner
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.utils.Translator
import al132.alib.utils.extensions.get
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by al132 on 1/16/2017.
 */
class GuiChemicalCombiner(playerInv: InventoryPlayer, tile: TileChemicalCombiner) :
        GuiBase<TileChemicalCombiner>(ContainerChemicalCombiner(playerInv, tile), tile, GuiChemicalCombiner.textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "chemical_combiner_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.chemical_combiner.name")

    lateinit var toggleRecipeLock: GuiButton
    lateinit var pauseButton: GuiButton


    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 8, 16, 60, tile::energyStorage))
    }

    override fun actionPerformed(guibutton: GuiButton) {
        when (guibutton.id) {
            toggleRecipeLock.id -> PacketHandler.INSTANCE!!.sendToServer(ChemicalCombinerPacket(tile.pos, lock = true))
            pauseButton.id      -> PacketHandler.INSTANCE!!.sendToServer(ChemicalCombinerPacket(tile.pos, pause = true))
        }
    }

    override fun initGui() {
        super.initGui()
        toggleRecipeLock = GuiButton(0, this.guiLeft + 30, this.guiTop + 75, 80, 20, "Test")
        this.buttonList.add(toggleRecipeLock)

        pauseButton = GuiButton(1, this.guiLeft + 30, this.guiTop + 100, 80, 20, "Test")
        this.buttonList.add(pauseButton)
    }

    fun updateButtonStrings() {
        if (tile.recipeIsLocked) toggleRecipeLock.displayString = Translator.translateToLocal("tile.combiner.unlock_recipe")
        else toggleRecipeLock.displayString = Translator.translateToLocal("tile.combiner.lock_recipe")

        if (tile.paused) pauseButton.displayString = Translator.translateToLocal("tile.combiner.resume")
        else pauseButton.displayString = Translator.translateToLocal("tile.combiner.pause")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        updateButtonStrings()
        toggleRecipeLock.drawButtonForegroundLayer(mouseX, mouseY)
        pauseButton.drawButtonForegroundLayer(mouseX, mouseY)

        if (!tile.clientRecipeTarget.getStackInSlot(0).isEmpty) {
            this.drawItemStack(tile.clientRecipeTarget[0], 130, 21, Translator.translateToLocal("tile.combiner.target"))
        }
    }


    fun drawItemStack(stack: ItemStack, x: Int, y: Int, text: String?) {
        this.zLevel = 200.0f
        this.fontRenderer.drawString(text ?: "", x, y, Color.BLACK.rgb)
        this.zLevel = 0.0f
        /* RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
         GlStateManager.translate(0.0f, 0.0f, 32.0f)
         this.zLevel = 200.0f
         this.itemRender.zLevel = 200.0f
         this.itemRender.renderItemAndEffectIntoGUI(stack, x, y)
         this.itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y + 5, altText)
         this.zLevel = 0.0f
         this.itemRender.zLevel = 0.0f*/
    }

}