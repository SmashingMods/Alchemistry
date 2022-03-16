package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.Registration;
import al132.alib.container.ABaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class DissolverContainer extends ABaseContainer {

    public DissolverContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.DISSOLVER_CONTAINER.get(), id, world, pos, playerInv,11);
        DissolverTile tile = (DissolverTile) world.getBlockEntity(pos);
        addSlot(new SlotItemHandler(tile.getInput(), 0, 83, 14));
        addSlotArray(0, 48, 85, 2, 5, tile.getOutput());
        addPlayerSlots();
    }



    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}