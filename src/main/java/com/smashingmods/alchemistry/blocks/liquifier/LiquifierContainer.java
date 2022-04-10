package com.smashingmods.alchemistry.blocks.liquifier;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemylib.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class LiquifierContainer extends BaseContainer {
    public LiquifierContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.LIQUIFIER_CONTAINER.get(), id, world, pos, playerInv, 1);
        LiquifierTile tile = (LiquifierTile) world.getBlockEntity(pos);
        this.addSlot(new SlotItemHandler(tile.getInput(), 0, 49, 58));
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
        return ((LiquifierTile) tile).progressTicks;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
