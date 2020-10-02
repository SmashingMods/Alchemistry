package al132.alchemistry.utils;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

public class TagUtils {

    public static ITag<Item> tag(String tagName) {
        return TagCollectionManager.getManager().getItemTags().get(new ResourceLocation(tagName));
    }

    public static Ingredient tagIngredient(String tagName){
        return Ingredient.fromTag(tag(tagName));
    }
}
