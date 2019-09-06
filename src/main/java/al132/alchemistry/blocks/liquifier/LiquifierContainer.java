package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LiquifierContainer extends ABaseContainer {
    public LiquifierContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.liquifierContainer, id, world, pos, playerInv, player, 1);
        LiquifierTile liquifier = (LiquifierTile) world.getTileEntity(pos);
        this.addSlot(new SlotItemHandler(liquifier.getInput(), 0, 49, 58));
        addPlayerSlots();
    }

    public IEnergyStorage getEnergy() {
        return ((LiquifierTile) this.tile).energy;
    }

    public IFluidHandler getTank() {
        return ((LiquifierTile) this.tile).outputTank;
    }

}
