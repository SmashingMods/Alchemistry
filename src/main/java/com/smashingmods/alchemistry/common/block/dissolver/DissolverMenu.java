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
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class DissolverMenu extends AbstractProcessingMenu {

    public DissolverMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())));
    }

    protected DissolverMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.DISSOLVER_MENU.get(), pContainerId, pInventory, pBlockEntity, 1, 12);

        DissolverBlockEntity blockEntity = (DissolverBlockEntity) pBlockEntity;
        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();

        // input
        addSlots(SlotItemHandler::new, inputHandler, 1, 1, 0, inputHandler.getSlots(), 48, 31);
        // output 4x3 grid
        addSlots(SlotItemHandler::new, outputHandler, 3, 4, 0, outputHandler.getSlots(), 102, 13);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(getBlockEntity().getLevel()), getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.DISSOLVER.get());
    }
}
