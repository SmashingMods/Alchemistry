//package com.smashingmods.alchemistry.common.block.oldblocks.fusion;
//
//import com.smashingmods.alchemistry.common.block.oldblocks.container.BaseContainer;
//import com.smashingmods.alchemistry.registry.MenuRegistry;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.DataSlot;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.items.SlotItemHandler;
//import org.apache.commons.lang3.BooleanUtils;
//
//import java.util.Objects;
//
//public class FusionContainer extends BaseContainer {
//    public FusionContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
//        super(MenuRegistry.FUSION_CONTAINER.get(), pId, pBlockPos, pInventory, 3);
//        Level level = pInventory.player.getLevel();
//        FusionBlockEntity blockEntity = (FusionBlockEntity) level.getBlockEntity(pBlockPos);
//        this.addSlotArray(0, 44, 79, 1, 2, Objects.requireNonNull(blockEntity).getInputHandler());
//        this.addSlot(new SlotItemHandler(blockEntity.getOutputHandler(), 0, 132, 79));
//        addPlayerSlots();
//        trackInt(new DataSlot() {
//            @Override
//            public int get() {
//                return blockEntity.progressTicks;
//            }
//
//            @Override
//            public void set(int value) {
//                blockEntity.progressTicks = value;
//            }
//        });
//        trackInt(new DataSlot() {
//            @Override
//            public int get() {
//                return BooleanUtils.toInteger(blockEntity.isValidMultiblock);
//            }
//
//            @Override
//            public void set(int value) {
//                blockEntity.isValidMultiblock = (value == 1);
//            }
//        });
//    }
//
//    public int getProgressTicks() {
//        return ((FusionBlockEntity) blockEntity).progressTicks;
//    }
//
//
//    @Override
//    public boolean stillValid(Player pPlayer) {
//        return true;
//    }
//
//    public boolean isValidMultiblock() {
//        return ((FusionBlockEntity) blockEntity).isValidMultiblock;
//    }
//}
