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
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class FusionControllerMenu extends AbstractProcessingMenu {

    public FusionControllerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level().getBlockEntity(pBuffer.readBlockPos())));
    }

    protected FusionControllerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.FUSION_CONTROLLER_MENU.get(), pContainerId, pInventory, pBlockEntity, 2, 1);

        FusionControllerBlockEntity blockEntity = (FusionControllerBlockEntity) pBlockEntity;
        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();

        addSlots(SlotItemHandler::new, inputHandler, 0, inputHandler.getSlots(), 48, 18);
        addSlots(SlotItemHandler::new, inputHandler, 1, inputHandler.getSlots(), 48, 44);
        addSlots(SlotItemHandler::new, outputHandler, 120, 31);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(this.getBlockEntity().getLevel()), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.FUSION_CONTROLLER.get());
    }
}
