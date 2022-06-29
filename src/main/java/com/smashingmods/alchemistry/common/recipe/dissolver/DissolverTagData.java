package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.utils.StackUtils;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DissolverTagData {

    private final String prefix;
    private final int count;
    private final List<String> strings;

    public DissolverTagData(String pPrefix, int pCount, List<String> pStrings) {
        this.prefix = pPrefix;
        this.count = pCount;
        this.strings = pStrings;
    }

    public List<String> getStrings() {
        return strings;
    }

    public ItemStack getStack(int pIndex) {
        return StackUtils.toStack(strings.get(pIndex), count);
    }

    public String toForgeTag(int pIndex) {
        return String.format("forge:%s/%s", prefix, strings.get(pIndex));
    }
}
