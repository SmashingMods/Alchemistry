package com.smashingmods.alchemistry.common.block.atomizer;

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

public class AtomizerMenu extends AbstractProcessingMenu {

    public AtomizerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())));
    }

    protected AtomizerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.ATOMIZER_MENU.get(), pContainerId, pInventory, pBlockEntity, 0, 1);
        AtomizerBlockEntity blockEntity = (AtomizerBlockEntity) pBlockEntity;
        ProcessingSlotHandler outputHandler = blockEntity.getSlotHandler();
        addSlots(SlotItemHandler::new, outputHandler, 1, 1, 0, outputHandler.getSlots(), 98, 35);
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
        Objects.requireNonNull(this.getBlockEntity().getLevel());
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.ATOMIZER.get());
    }
}
