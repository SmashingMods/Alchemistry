package al132.alchemistry.data;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class DatagenUtils {

    public static void addStackToJson(JsonObject json, String key, ItemStack stack) {
        if (!stack.isEmpty()) {
            JsonObject tempJson = new JsonObject();
            tempJson.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) tempJson.addProperty("count", stack.getCount());
            json.add(key, tempJson);
        }
    }
}
