package al132.alchemistry.blocks.fusion;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;

public class FusionContainer extends ABaseContainer {
    public FusionContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.fusionContainer, id, world, pos, playerInv, player, 3);
        FusionTile tile = (FusionTile)world.getTileEntity(pos);
        this.addSlotArray(0,44, 79, 1, 2, tile.getInput());
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 132, 79));
        addPlayerSlots();
    }

    public IEnergyStorage getEnergy() {
        return ((FusionTile) this.tile).energy;
    }

}
