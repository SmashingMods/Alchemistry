package al132.alchemistry.utils;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class TagUtils {

    public static ITag<Item> tag(String tagName) {
        return ItemTags.getCollection().getTagByID(new ResourceLocation(tagName));
        //return ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS,new ResourceLocation(tagName));
        //return TagCollectionManager.getManager().getItemTags().get(new ResourceLocation(tagName));
    }

    public static Ingredient tagIngredient(String tagName){
        return Ingredient.fromTag(tag(tagName));
    }
}
