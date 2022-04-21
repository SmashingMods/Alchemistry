//package com.smashingmods.alchemistry.common.block.oldblocks.dissolver;
//
//import com.smashingmods.alchemistry.common.block.oldblocks.container.BaseContainer;
//import com.smashingmods.alchemistry.registry.MenuRegistry;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.items.SlotItemHandler;
//
//public class DissolverContainer extends BaseContainer {
//
//    public DissolverContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
//        super(MenuRegistry.DISSOLVER_CONTAINER.get(), pId, pBlockPos, pInventory,11);
//        Level level = pInventory.player.getLevel();
//        DissolverBlockEntity blockEntity = (DissolverBlockEntity) level.getBlockEntity(pBlockPos);
//        addSlot(new SlotItemHandler(blockEntity.getInputHandler(), 0, 83, 14));
//        addSlotArray(0, 48, 85, 2, 5, blockEntity.getOutputHandler());
//        addPlayerSlots();
//    }
//
//    @Override
//    public boolean stillValid(Player pPlayer) {
//        return true;
//    }
//}