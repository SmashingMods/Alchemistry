package com.smashingmods.alchemistry.common.block.fusion;

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


public class FusionControllerMenu extends AbstractProcessingMenu {

    public FusionControllerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected FusionControllerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.FUSION_CONTROLLER_MENU.get(), pContainerId, pInventory, pBlockEntity, 2, 1);
        FusionControllerBlockEntity blockEntity = (pBlockEntity instanceof FusionControllerBlockEntity be) ? be : null;
        ProcessingSlotHandler inputHandler = (blockEntity != null) ? blockEntity.getInputHandler() : new ProcessingSlotHandler(2);
        ProcessingSlotHandler outputHandler = (blockEntity != null) ? blockEntity.getOutputHandler() : new ProcessingSlotHandler(1);
        addSlots(SlotItemHandler::new, inputHandler, 0, inputHandler.getSlots(), 48, 18);
        addSlots(SlotItemHandler::new, inputHandler, 1, inputHandler.getSlots(), 48, 44);
        addSlots(SlotItemHandler::new, outputHandler, 120, 31);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.FUSION_CONTROLLER.get());
    }
}
