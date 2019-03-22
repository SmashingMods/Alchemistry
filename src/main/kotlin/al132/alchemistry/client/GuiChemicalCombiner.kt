package al132.alchemistry.client

import al132.alchemistry.compat.jei.Translator
import al132.alchemistry.network.ChemicalCombinerPacket
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.tiles.TileChemicalCombiner
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.IResource
import al132.alib.utils.extensions.get
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiChemicalCombiner(playerInv: InventoryPlayer, tile: TileChemicalCombiner) :
        GuiBase<TileChemicalCombiner>(ContainerChemicalCombiner(playerInv, tile), tile) {

    override val displayName = Translator.translateToLocal("tile.chemical_combiner.name")

    lateinit var toggleRecipeLock: GuiButton
    lateinit var pauseButton: GuiButton


    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 8, 16, 60, tile::energyCapability))
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
            this.drawItemStack(tile.clientRecipeTarget[0], 140, 5, Translator.translateToLocal("tile.combiner.target"))
        }
    }


    fun drawItemStack(stack: ItemStack, x: Int, y: Int, altText: String?) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.translate(0.0f, 0.0f, 32.0f)
        this.zLevel = 200.0f
        this.itemRender.zLevel = 200.0f
        //var font: net.minecraft.client.gui.FontRenderer? = stack.item.getFontRenderer(stack)
        //if (font == null) font = fontRenderer
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y)
        //this.itemRender.renderItemOverlayIntoGUI(font!!, stack, x, y, altText)
        this.itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y + 5, altText)
        this.zLevel = 0.0f
        this.itemRender.zLevel = 0.0f
    }

    companion object : IResource {
        override fun textureLocation() = ResourceLocation(GuiBase.root + "chemical_combiner_gui.png")
    }
}