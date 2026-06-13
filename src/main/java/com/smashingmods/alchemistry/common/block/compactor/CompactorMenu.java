package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingMenu;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;


public class CompactorMenu extends AbstractProcessingMenu {

    public CompactorMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected CompactorMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.COMPACTOR_MENU.get(), pContainerId, pInventory, pBlockEntity, 2, 1);
        CompactorBlockEntity blockEntity = (pBlockEntity instanceof CompactorBlockEntity be) ? be : null;
        ProcessingSlotHandler inputHandler = (blockEntity != null) ? blockEntity.getInputHandler() : new ProcessingSlotHandler(2);
        ProcessingSlotHandler outputHandler = (blockEntity != null) ? blockEntity.getOutputHandler() : new ProcessingSlotHandler(1);
        addSlots(SlotItemHandler::new, inputHandler, 0, inputHandler.getSlots(), 48, 50);
        addSlots(SlotItemHandler::new, outputHandler, 120, 50);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(getBlockEntity().getLevel(), getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.COMPACTOR.get());
    }
}
