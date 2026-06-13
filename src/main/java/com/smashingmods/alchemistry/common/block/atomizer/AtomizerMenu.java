package com.smashingmods.alchemistry.common.block.atomizer;

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


public class AtomizerMenu extends AbstractProcessingMenu {

    public AtomizerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected AtomizerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.ATOMIZER_MENU.get(), pContainerId, pInventory, pBlockEntity, 0, 1);
        AtomizerBlockEntity blockEntity = (pBlockEntity instanceof AtomizerBlockEntity be) ? be : null;
        ProcessingSlotHandler outputHandler = (blockEntity != null) ? blockEntity.getOutputHandler() : new ProcessingSlotHandler(1);
        addSlots(SlotItemHandler::new, outputHandler, 1, 1, 0, outputHandler.getSlots(), 120, 31);
    }

    @Override
    protected boolean isFluidMachine() {
        return true;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.ATOMIZER.get());
    }
}
