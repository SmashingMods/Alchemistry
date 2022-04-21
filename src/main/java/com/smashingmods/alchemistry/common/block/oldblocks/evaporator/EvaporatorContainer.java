//package com.smashingmods.alchemistry.common.block.oldblocks.evaporator;
//
//import com.smashingmods.alchemistry.common.block.oldblocks.container.BaseContainer;
//import com.smashingmods.alchemistry.registry.MenuRegistry;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.items.SlotItemHandler;
//
//import java.util.Objects;
//
//public class EvaporatorContainer extends BaseContainer {
//    public EvaporatorContainer(int pId, BlockPos pBlockPos, Inventory pInventory) {
//        super(MenuRegistry.EVAPORATOR_CONTAINER.get(), pId, pBlockPos, pInventory, 1);
//        Level level = pInventory.player.getLevel();
//        EvaporatorBlockEntity blockEntity = (EvaporatorBlockEntity) level.getBlockEntity(pBlockPos);
//        addSlot(new SlotItemHandler(Objects.requireNonNull(blockEntity).getOutputHandler(), 0, 122, 52));
//        addPlayerSlots();
//
//    }
//
//    @Override
//    public boolean stillValid(Player pPlayer) {
//        return true;
//    }
//}