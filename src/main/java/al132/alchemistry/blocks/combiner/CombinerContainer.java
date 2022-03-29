package al132.alchemistry.blocks.combiner;

import al132.alchemistry.Registration;
import al132.alib.container.ABaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class CombinerContainer extends ABaseContainer {

    public CombinerContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.COMBINER_CONTAINER.get(), id, world, pos, playerInv, 10);
        CombinerTile tile = (CombinerTile) world.getBlockEntity(pos);
        this.addSlotArray(0, 39, 14, 3, 3, tile.getInput());
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 140, 33));
        addPlayerSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}