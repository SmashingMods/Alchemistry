package al132.alchemistry.blocks.fission;

import al132.alchemistry.Registration;
import al132.alib.container.ABaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class FissionContainer extends ABaseContainer {
    public FissionContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.FISSION_CONTAINER.get(), id, world, pos, playerInv, 3);
        FissionTile tile = (FissionTile) world.getBlockEntity(pos);
        this.addSlot(new SlotItemHandler(tile.getInput(), 0, 49, 60));
        this.addSlotArray(0, 122, 60, 1, 2, tile.getOutput());
        addPlayerSlots();
    }

    //public IEnergyStorage getEnergy() {
    //    return ((FissionTile) this.tile).energy;
    //}

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
