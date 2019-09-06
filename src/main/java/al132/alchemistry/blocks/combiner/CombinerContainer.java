package al132.alchemistry.blocks.combiner;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;

public class CombinerContainer extends ABaseContainer {

    public CombinerContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.combinerContainer, id, world, pos, playerInv, player, 10);
        CombinerTile tile = (CombinerTile) world.getTileEntity(pos);
        this.addSlotArray(0, 39, 14, 3, 3, tile.getInput());
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 140, 33));
        addPlayerSlots();
    }

    public IEnergyStorage getEnergy() {
        return ((CombinerTile) tile).energy;
    }

    public boolean isLocked(){
        return ((CombinerTile)tile).recipeIsLocked;
    }
}