package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.Registration;
import al132.alchemistry.blocks.atomizer.AtomizerTile;
import al132.alib.container.ABaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;

public class EvaporatorContainer extends ABaseContainer {
    public EvaporatorContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.EVAPORATOR_CONTAINER.get(), id, world, pos, playerInv, 1);
        EvaporatorTile tile = (EvaporatorTile) world.getBlockEntity(pos);
        addSlot(new SlotItemHandler(tile.getOutput(), 0, 122, 52));
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
        return ((EvaporatorTile) tile).progressTicks;
    }

    public IFluidHandler getFluids() {
        return ((EvaporatorTile) tile).fluidTank;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}