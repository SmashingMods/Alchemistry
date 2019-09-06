package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;

public class DissolverContainer extends ABaseContainer {

    public DissolverContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.dissolverContainer, id, world, pos, playerInv, player, 11);
        DissolverTile tile = (DissolverTile) world.getTileEntity(pos);
        addSlot(new SlotItemHandler(tile.getInput(), 0, 83, 14));
        addSlotArray(0, 48, 85, 2, 5, tile.getOutput());
        addPlayerSlots();
    }


    public IEnergyStorage getEnergy() {
        return ((DissolverTile) tile).energy;
    }
}