package com.smashingmods.alchemistry.blocks.combiner;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemylib.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class CombinerContainer extends BaseContainer {

    public CombinerContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.COMBINER_CONTAINER.get(), id, world, pos, playerInv, 10);
        CombinerTile tile = (CombinerTile) world.getBlockEntity(pos);
        this.addSlotArray(0, 39, 14, 3, 3, tile.getInput());
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 140, 33));
        addPlayerSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}