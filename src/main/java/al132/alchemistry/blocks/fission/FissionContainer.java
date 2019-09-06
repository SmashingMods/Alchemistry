package al132.alchemistry.blocks.fission;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;

public class FissionContainer extends ABaseContainer {
    public FissionContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.fissionContainer, id, world, pos, playerInv, player, 3);
        FissionTile tile = (FissionTile) world.getTileEntity(pos);
        this.addSlot(new SlotItemHandler(tile.getInput(), 0, 49, 60));
        this.addSlotArray(0, 122, 60, 1, 2, tile.getOutput());
        addPlayerSlots();
    }

    public IEnergyStorage getEnergy() {
        return ((FissionTile) this.tile).energy;
    }

}
