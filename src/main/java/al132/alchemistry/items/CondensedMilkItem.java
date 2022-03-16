package al132.alchemistry.items;

import al132.alchemistry.setup.ModSetup;
import al132.alib.items.ABaseItem;
import net.minecraft.world.item.Item;

public class CondensedMilkItem extends ABaseItem {
    public CondensedMilkItem() {
        super(ModSetup.ITEM_GROUP, new Item.Properties());//"condensed_milk");
    }
}
