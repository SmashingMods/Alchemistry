package al132.alchemistry.blocks;

import al132.alib.tiles.ABaseInventoryTile;
import al132.alib.tiles.AutomationStackHandler;
import al132.alib.tiles.GuiTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;

abstract public class AlchemistryBaseTile extends ABaseInventoryTile implements GuiTile {

    private int dirtyTicks = 0;

    public AlchemistryBaseTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public void markDirtyGUIEvery(int ticks) {
        this.dirtyTicks++;
        if (this.dirtyTicks >= ticks) {
            this.setChanged();
            this.dirtyTicks = 0;
        }
    }

    @Override
    public AutomationStackHandler initAutomationInput(IItemHandlerModifiable input) {
        return new AutomationStackHandler(input) {
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler initAutomationOutput(IItemHandlerModifiable output) {
        return new AutomationStackHandler(output) {
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!getStackInSlot(slot).isEmpty()) return super.extractItem(slot, amount, simulate);
                else return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public int getHeight() {
        return 222;
    }

    @Override
    public int getWidth() {
        return 174;
    }
}
