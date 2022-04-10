package com.smashingmods.alchemistry.blocks.fusion;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemylib.container.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.BooleanUtils;

public class FusionContainer extends BaseContainer {
    public FusionContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.FUSION_CONTAINER.get(), id, world, pos, playerInv, 3);
        FusionTile tile = (FusionTile) world.getBlockEntity(pos);
        this.addSlotArray(0, 44, 79, 1, 2, tile.getInput());
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 132, 79));
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
        return ((FusionTile) tile).progressTicks;
    }


    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }

    public boolean isValidMultiblock() {
        return ((FusionTile) tile).isValidMultiblock;
    }
}
