package com.smashingmods.alchemistry.blocks.liquifier;

import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class LiquifierContainer extends BaseContainer {
    public LiquifierContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
        super(Registry.LIQUIFIER_CONTAINER.get(), pId, pBlockPos, pInventory, 1);
        Level level = pInventory.player.getLevel();
        LiquifierBlockEntity tile = (LiquifierBlockEntity) level.getBlockEntity(pBlockPos);
        this.addSlot(new SlotItemHandler(tile.getInputHandler(), 0, 49, 58));
        addPlayerSlots();
        trackInt(new DataSlot() {
            @Override
            public int get() {
                return tile.progressTicks;
            }
            @Override
            public void set(int value) {
                tile.progressTicks = value;
            }
        });
    }

    public int getProgressTicks() {
        return ((LiquifierBlockEntity) baseBlockEntity).progressTicks;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
