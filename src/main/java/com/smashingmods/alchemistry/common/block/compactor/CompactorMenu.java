package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class CompactorMenu extends AbstractAlchemistryMenu {

    public CompactorMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())));
    }

    protected CompactorMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.COMPACTOR_MENU.get(), pContainerId, pInventory, pBlockEntity, 2, 1);

        CompactorBlockEntity blockEntity = (CompactorBlockEntity) pBlockEntity;
        CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
        CustomItemStackHandler outputHandler = blockEntity.getOutputHandler();

        addSlots(SlotItemHandler::new, inputHandler, 0, inputHandler.getSlots(), 51, 35);
        addSlots(SlotItemHandler::new, inputHandler, 1, inputHandler.getSlots(), 80, 12);
        addSlots(SlotItemHandler::new, outputHandler, 111, 35);
    }

    @Override
    public void addPlayerInventorySlots(Inventory pInventory) {
        addSlots(Slot::new, pInventory, 3, 9, 9, 27, 8, 84);
        addSlots(Slot::new, pInventory, 1, 9, 0, 9, 8, 142);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        Objects.requireNonNull(this.getBlockEntity().getLevel());
        return stillValid(ContainerLevelAccess.create(getBlockEntity().getLevel(), getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.COMPACTOR.get());
    }
}
