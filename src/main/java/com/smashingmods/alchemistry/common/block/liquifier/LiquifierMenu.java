package com.smashingmods.alchemistry.common.block.liquifier;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class LiquifierMenu extends AbstractProcessingMenu {

    public LiquifierMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())));
    }

    protected LiquifierMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.LIQUIFIER_MENU.get(), pContainerId, pInventory, pBlockEntity, 1, 0);
        LiquifierBlockEntity blockEntity = (LiquifierBlockEntity) pBlockEntity;
        ProcessingSlotHandler inputHandler = blockEntity.getSlotHandler();
        addSlots(SlotItemHandler::new, inputHandler, 1, 1, 0, inputHandler.getSlots(), 62, 35);
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
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(this.getBlockEntity().getLevel()), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.LIQUIFIER.get());
    }
}
