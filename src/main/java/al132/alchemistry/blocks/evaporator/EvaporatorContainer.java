package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.Ref;
import al132.alib.container.ABaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;

public class EvaporatorContainer extends ABaseContainer {
    public EvaporatorContainer(int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(Ref.evaporatorContainer, id, world, pos, playerInv, player, 1);
        EvaporatorTile tile = (EvaporatorTile) world.getTileEntity(pos);
        addSlot(new SlotItemHandler(tile.getOutput(), 0, 122, 52));
        addPlayerSlots();
    }

    public IFluidHandler getFluids() {
        return ((EvaporatorTile) tile).fluidTank;
    }
}