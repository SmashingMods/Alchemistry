package al132.alchemistry.items;

import al132.alchemistry.Alchemistry;
import al132.alib.items.ABaseItem;

public class SlotFillerItem extends ABaseItem {

    public SlotFillerItem() {
        super(Alchemistry.data, "slot_filler", new Properties().maxStackSize(1));
    }
}
