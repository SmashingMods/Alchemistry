package al132.alchemistry.core;

import al132.alchemistry.chemistry.ChemicalElement;
import al132.alchemistry.chemistry.ElementRegistry;
import al132.alchemistry.items.ModItems;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class AlchemistryCoreUtils {

    private static int textColor = new Color(225, 225, 225).getRGB();

    public static void renderAbbreviation(Object fr, Object s, int x, int y) {
        FontRenderer fontRenderer = (FontRenderer) fr;
        ItemStack stack = (ItemStack) s;

        if (!stack.isEmpty()) {
            if (stack.getItem() == ModItems.INSTANCE.getElements()) {
                int meta = stack.getMetadata();
                ChemicalElement element = ElementRegistry.INSTANCE.get(meta);
                if (element != null) {
                    String str = element.getAbbreviation();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.disableBlend();
                    int height = fontRenderer.FONT_HEIGHT;
                    fontRenderer.FONT_HEIGHT -= 8;
                    fontRenderer.drawStringWithShadow(str, (float) x, (float) y, textColor);
                    fontRenderer.FONT_HEIGHT = height;
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    GlStateManager.enableBlend();
                }
            }
        }
    }
}

/*
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                fr.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableBlend();
            }

 */