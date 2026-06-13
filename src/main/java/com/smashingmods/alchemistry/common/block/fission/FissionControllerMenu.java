package com.smashingmods.alchemistry.common.block.fission;

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


public class FissionControllerMenu extends AbstractProcessingMenu {

    public FissionControllerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected FissionControllerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.FISSION_CONTROLLER_MENU.get(), pContainerId, pInventory, pBlockEntity, 1, 2);
        FissionControllerBlockEntity blockEntity = (pBlockEntity instanceof FissionControllerBlockEntity be) ? be : null;
        ProcessingSlotHandler inputHandler = (blockEntity != null) ? blockEntity.getInputHandler() : new ProcessingSlotHandler(1);
        ProcessingSlotHandler outputHandler = (blockEntity != null) ? blockEntity.getOutputHandler() : new ProcessingSlotHandler(2);
        addSlots(SlotItemHandler::new, inputHandler, 48, 31);
        addSlots(SlotItemHandler::new, outputHandler, 0, outputHandler.getSlots(), 120, 18);
        addSlots(SlotItemHandler::new, outputHandler, 1, outputHandler.getSlots(), 120, 44);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.FISSION_CONTROLLER.get());
    }
}
