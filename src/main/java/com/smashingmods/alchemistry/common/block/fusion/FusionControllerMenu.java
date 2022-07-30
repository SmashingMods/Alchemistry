package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class FusionControllerMenu extends AbstractAlchemistryMenu {

    protected final ContainerData containerData;

    public FusionControllerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())), new SimpleContainerData(4));
    }

    protected FusionControllerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(MenuRegistry.FUSION_CONTROLLER_MENU.get(), pContainerId, pInventory, pBlockEntity, pContainerData, 2, 1);
        this.containerData = pContainerData;

        FusionControllerBlockEntity blockEntity = (FusionControllerBlockEntity) pBlockEntity;
        CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
        CustomItemStackHandler outputHandler = blockEntity.getOutputHandler();

        addSlots(SlotItemHandler::new, inputHandler, 0, inputHandler.getSlots(), 44, 35);
        addSlots(SlotItemHandler::new, inputHandler, 1, inputHandler.getSlots(), 62, 35);
        addSlots(SlotItemHandler::new, outputHandler, 134, 35);
    }

    @Override
    public void addPlayerInventorySlots(Inventory pInventory) {
        addSlots(Slot::new, pInventory, 3, 9, 9, 27, 8, 84);
        addSlots(Slot::new, pInventory, 1, 9, 0, 9, 8, 142);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(this.getBlockEntity().getLevel()), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.FUSION_CONTROLLER.get());
    }
}
