package com.smashingmods.alchemistry.blocks.combiner;

import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CombinerContainer extends BaseContainer {

    public CombinerContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
        super(Registry.COMBINER_CONTAINER.get(), pId, pBlockPos, pInventory, 10);
        Level level = pInventory.player.getLevel();
        CombinerBlockEntity blockEntity = (CombinerBlockEntity) level.getBlockEntity(pBlockPos);
        this.addSlotArray(0, 39, 14, 3, 3, Objects.requireNonNull(blockEntity).getInputHandler());
        this.addSlot(new SlotItemHandler(blockEntity.getOutputHandler(), 0, 140, 33));
        addPlayerSlots();
    }

    @Override
    public boolean stillValid(@Nonnull Player pPlayer) {
        return true;
    }
}