package com.smashingmods.alchemistry.blocks.evaporator;

import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class EvaporatorContainer extends BaseContainer {
    public EvaporatorContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
        super(Registry.EVAPORATOR_CONTAINER.get(), pId, pBlockPos, pInventory, 1);
        Level level = pInventory.player.getLevel();
        EvaporatorBlockEntity blockEntity = (EvaporatorBlockEntity) level.getBlockEntity(pBlockPos);
        addSlot(new SlotItemHandler(Objects.requireNonNull(blockEntity).getOutputHandler(), 0, 122, 52));
        addPlayerSlots();

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}