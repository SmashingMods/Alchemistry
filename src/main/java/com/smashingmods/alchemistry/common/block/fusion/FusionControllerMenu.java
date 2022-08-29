package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.api.blockentity.container.AbstractProcessingMenu;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.network.BlockEntityPacket;
import com.smashingmods.alchemistry.common.network.PacketHandler;
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

public class FusionControllerMenu extends AbstractProcessingMenu {

    public FusionControllerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())));
    }

    protected FusionControllerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.FUSION_CONTROLLER_MENU.get(), pContainerId, pInventory, pBlockEntity, 2, 1);

        FusionControllerBlockEntity blockEntity = (FusionControllerBlockEntity) pBlockEntity;
        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();

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
    public void broadcastChanges() {
        super.broadcastChanges();
        PacketHandler.sendToNear(
                new BlockEntityPacket(getBlockEntity().getBlockPos(), getBlockEntity().getUpdateTag()),
                getLevel(),
                getBlockEntity().getBlockPos(),
                64
        );
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(this.getBlockEntity().getLevel()), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.FUSION_CONTROLLER.get());
    }
}
