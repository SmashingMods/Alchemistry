//package com.smashingmods.alchemistry.common.block.oldblocks.atomizer;
//
//import com.smashingmods.alchemistry.registry.MenuRegistry;
//import com.smashingmods.alchemistry.common.block.oldblocks.container.BaseContainer;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.items.SlotItemHandler;
//
//import javax.annotation.Nonnull;
//
//public class AtomizerContainer extends BaseContainer {
//
//    public AtomizerContainer(int pContainerId, BlockPos pBlockPos, Inventory pInventory) {
//        super(MenuRegistry.ATOMIZER_CONTAINER.get(), pContainerId, pBlockPos, pInventory, 1);
//        Level level = pInventory.player.getLevel();
//        AtomizerBlockEntity blockEntity = (AtomizerBlockEntity) level.getBlockEntity(pBlockPos);
//        this.addSlot(new SlotItemHandler(blockEntity.getOutputHandler(), 0, 122, 52));
//        addPlayerSlots();
//     /*   trackInt(new DataSlot() {
//            @Override
//            public int get() {
//                return tile.progressTicks;
//            }
//
//            @Override
//            public void set(int value) {
//                tile.progressTicks = value;
//            }
//        });
//    }
//
//    public int getProgressTicks() {
//        return ((AtomizerTile) tile).progressTicks;
//    }*/
//    }
//
//    //public IEnergyStorage getEnergy() {
//    //     return ((AtomizerTile) this.tile).energy;
//    //}
//
//    @Override
//    public boolean stillValid(@Nonnull Player pPlayer) {
//        return true;
//    }
//}
