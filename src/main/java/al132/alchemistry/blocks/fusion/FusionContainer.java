package al132.alchemistry.blocks.fusion;

import al132.alchemistry.Registration;
import al132.alib.container.ABaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class FusionContainer extends ABaseContainer {
    public FusionContainer(int id, Level world, BlockPos pos, Inventory playerInv) {
        super(Registration.FUSION_CONTAINER.get(), id, world, pos, playerInv, 3);
        FusionTile tile = (FusionTile) world.getBlockEntity(pos);
        this.addSlotArray(0, 44, 79, 1, 2, tile.getInput());
        this.addSlot(new SlotItemHandler(tile.getOutput(), 0, 132, 79));
        addPlayerSlots();
    }


    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
