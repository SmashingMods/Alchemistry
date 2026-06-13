package com.smashingmods.alchemistry.common.block.dissolver;

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


public class DissolverMenu extends AbstractProcessingMenu {

    public DissolverMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected DissolverMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.DISSOLVER_MENU.get(), pContainerId, pInventory, pBlockEntity, 1, 12);
        DissolverBlockEntity blockEntity = (pBlockEntity instanceof DissolverBlockEntity be) ? be : null;
        ProcessingSlotHandler inputHandler = (blockEntity != null) ? blockEntity.getInputHandler() : new ProcessingSlotHandler(1);
        ProcessingSlotHandler outputHandler = (blockEntity != null) ? blockEntity.getOutputHandler() : new ProcessingSlotHandler(12);
        addSlots(SlotItemHandler::new, inputHandler, 1, 1, 0, inputHandler.getSlots(), 48, 31);
        addSlots(SlotItemHandler::new, outputHandler, 3, 4, 0, outputHandler.getSlots(), 102, 13);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(getBlockEntity().getLevel(), getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.DISSOLVER.get());
    }
}
