package com.smashingmods.alchemistry.blocks.fission;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemylib.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.BooleanUtils;

public class FissionContainer extends BaseContainer {
    public FissionContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.FISSION_CONTAINER.get(), id, world, pos, playerInv, 3);
        FissionTile tile = (FissionTile) world.getBlockEntity(pos);
        this.addSlot(new SlotItemHandler(tile.getInput(), 0, 49, 60));
        this.addSlotArray(0, 122, 60, 1, 2, tile.getOutput());
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
        trackInt(new DataSlot() {
            @Override
            public int get() {
                return BooleanUtils.toInteger(tile.isValidMultiblock);
            }

            @Override
            public void set(int value) {
                tile.isValidMultiblock = (value == 1);
            }
        });
    }

    public int getProgressTicks() {
        return ((FissionTile) tile).progressTicks;
    }

    //public IEnergyStorage getEnergy() {
    //    return ((FissionTile) this.tile).energy;
    //}

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }

    public boolean isValidMultiblock() {
        return ((FissionTile) tile).isValidMultiblock;
    }
}
