package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AtomizerContainer extends ABaseContainer {
    public AtomizerContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.atomizerContainer, id, world, pos, playerInv, player, 1);
        AtomizerTile tile = (AtomizerTile) world.getTileEntity(pos);
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 122, 52));
        addPlayerSlots();
    }

    public IEnergyStorage getEnergy() {
        return ((AtomizerTile) this.tile).energy;
    }

    public IFluidHandler getTank() {
        return ((AtomizerTile) this.tile).inputTank;
    }
}
