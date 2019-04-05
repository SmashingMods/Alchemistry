package al132.alchemistry.items;

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.item.ItemStack
import java.awt.Color

public class TEISRElement : TileEntityItemStackRenderer() {

    override fun renderByItem(itemStackIn: ItemStack, partialTicks: Float) {
        val item = itemStackIn.item
        if(item == ModItems.elements) {
            Minecraft.getMinecraft().fontRenderer?.drawString("Fe", 0/*-5*/, 0, Color.BLACK.rgb)
            //GlStateManager.pushMatrix()
            //GlStateManager.popMatrix()
        }
        /*

        if (item === Items.SHIELD) {
            if (itemStackIn.getSubCompound("BlockEntityTag") != null) {
                //this.banner.setItemValues(itemStackIn, true)
                Minecraft.getMinecraft().textureManager.bindTexture(BannerTextures.SHIELD_DESIGNS.getResourceLocation(this.banner.patternResourceLocation, this.banner.patternList, this.banner.colorList)!!)
            } else {
                Minecraft.getMinecraft().textureManager.bindTexture(BannerTextures.SHIELD_BASE_TEXTURE)
            }

            GlStateManager.pushMatrix()
            GlStateManager.scale(1.0f, -1.0f, -1.0f)
            this.modelShield.render()
            GlStateManager.popMatrix()
        } else if (item === Items.SKULL) {
            var gameprofile: GameProfile? = null
            if (itemStackIn.hasTagCompound()) {
                val nbttagcompound = itemStackIn.tagCompound
                if (nbttagcompound!!.hasKey("SkullOwner", 10)) {
                    gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"))
                } else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
                    val gameprofile1 = GameProfile(null as UUID?, nbttagcompound.getString("SkullOwner"))
                    gameprofile = TileEntitySkull.updateGameProfile(gameprofile1)
                    nbttagcompound.removeTag("SkullOwner")
                    nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(NBTTagCompound(), gameprofile!!))
                }
            }

            if (TileEntitySkullRenderer.instance != null) {
                GlStateManager.pushMatrix()
                GlStateManager.disableCull()
                TileEntitySkullRenderer.instance.renderSkull(0.0f, 0.0f, 0.0f, EnumFacing.UP, 180.0f, itemStackIn.metadata, gameprofile, -1, 0.0f)
                GlStateManager.enableCull()
                GlStateManager.popMatrix()
            }
        } else if (Block.getBlockFromItem(item) !== Blocks.CHEST) {
            ForgeHooksClient.renderTileItem(itemStackIn.item, itemStackIn.metadata)
        } else {
            TileEntityRendererDispatcher.instance.render(this.chestBasic, 0.0, 0.0, 0.0, 0.0f, partialTicks)
        }*/
    }
}
