package al132.alchemistry.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class DatagenUtils {

    public static void addStackToJson(JsonObject json, String key, ItemStack stack) {
        if (!stack.isEmpty()) {
            JsonObject tempJson = new JsonObject();
            tempJson.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) tempJson.addProperty("count", stack.getCount());
            json.add(key, tempJson);
        }
    }

    public static void addStackListToJson(JsonObject json, String key, List<ItemStack> stacks) {
        if (!stacks.isEmpty()) {
            JsonArray tempJson = new JsonArray();
            for (ItemStack stack : stacks) {
                //System.out.println("Stack: " + stack.getItem().toString());
                JsonObject obj = new JsonObject();
                obj.addProperty("item",Registry.ITEM.getKey(stack.getItem()).toString());
                if (stack.getCount() > 1) obj.addProperty("count", stack.getCount());
                tempJson.add(obj);
            }
            json.add(key, tempJson);
        }
    }
}
