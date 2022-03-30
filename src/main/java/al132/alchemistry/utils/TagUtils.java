package al132.alchemistry.utils;


import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class TagUtils {

    public static TagKey<Item> tag(String tagName) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(tagName));

        //return ItemTags.getAllTags().getTag(new ResourceLocation(tagName));
        //return ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS,new ResourceLocation(tagName));
        //return TagCollectionManager.getManager().getItemTags().get(new ResourceLocation(tagName));
    }

    public static Ingredient tagIngredient(String tagName) {
        return Ingredient.of(tag(tagName));
        //return Ingredient.of(tag(tagName));
    }
}
