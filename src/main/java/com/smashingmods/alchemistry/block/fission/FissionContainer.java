package com.smashingmods.alchemistry.block.fission;

import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public class FissionContainer extends BaseContainer {
    public FissionContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
        super(Registry.FISSION_CONTAINER.get(), pId, pBlockPos, pInventory, 3);
        Level level = pInventory.player.getLevel();
        FissionBlockEntity blockEntity = (FissionBlockEntity) level.getBlockEntity(pBlockPos);
        this.addSlot(new SlotItemHandler(Objects.requireNonNull(blockEntity).getInputHandler(), 0, 49, 60));
        this.addSlotArray(0, 122, 60, 1, 2, blockEntity.getOutputHandler());
        addPlayerSlots();

        trackInt(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.progressTicks;
            }

            @Override
            public void set(int value) {
                blockEntity.progressTicks = value;
            }
        });

        trackInt(new DataSlot() {
            @Override
            public int get() {
                return BooleanUtils.toInteger(blockEntity.isValidMultiblock);
            }

            @Override
            public void set(int value) {
                blockEntity.isValidMultiblock = (value == 1);
            }
        });
    }

    public int getProgressTicks() {
        return ((FissionBlockEntity) blockEntity).progressTicks;
    }

    //public IEnergyStorage getEnergy() {
    //    return ((FissionTile) this.tile).energy;
    //}

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public boolean isValidMultiblock() {
        return ((FissionBlockEntity) blockEntity).isValidMultiblock;
    }
}
