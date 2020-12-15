package al132.alchemistry.blocks.dissolver;

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

    public String toOreData(int index) {
        return prefix + strs.get(index).substring(0, 1).toUpperCase() + strs.get(index).substring(1);
    }
}
