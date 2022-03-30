package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.utils.StackUtils;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DissolverTagData {

    public final String prefix;
    public final int quantity;
    public final List<String> strs;

    public DissolverTagData(String prefix, int quantity, List<String> strs) {
        this.prefix = prefix;
        this.quantity = quantity;
        this.strs = strs;
    }

    public ItemStack getStack(int index) {
        return StackUtils.toStack(strs.get(index), quantity);
    }

    public String toForgeTag(int index) {
        return "forge:" + prefix + "/" + strs.get(index);
        //prefix + strs.get(index).substring(0, 1).toUpperCase() + strs.get(index).substring(1);
    }
}
