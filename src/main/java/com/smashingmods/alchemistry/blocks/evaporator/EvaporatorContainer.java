package com.smashingmods.alchemistry.blocks.evaporator;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemylib.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class EvaporatorContainer extends BaseContainer {
    public EvaporatorContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.EVAPORATOR_CONTAINER.get(), id, world, pos, playerInv, 1);
        EvaporatorTile tile = (EvaporatorTile) world.getBlockEntity(pos);
        addSlot(new SlotItemHandler(tile.getOutput(), 0, 122, 52));
        addPlayerSlots();

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}