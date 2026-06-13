package com.smashingmods.alchemistry.common.block.liquifier;

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


public class LiquifierMenu extends AbstractProcessingMenu {

    public LiquifierMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected LiquifierMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.LIQUIFIER_MENU.get(), pContainerId, pInventory, pBlockEntity, 1, 0);
        LiquifierBlockEntity blockEntity = (pBlockEntity instanceof LiquifierBlockEntity be) ? be : null;
        ProcessingSlotHandler inputHandler = (blockEntity != null) ? blockEntity.getInputHandler() : new ProcessingSlotHandler(1);
        addSlots(SlotItemHandler::new, inputHandler, 1, 1, 0, inputHandler.getSlots(), 48, 31);
    }

    @Override
    protected boolean isFluidMachine() {
        return true;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.LIQUIFIER.get());
    }
}
