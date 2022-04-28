package com.smashingmods.alchemistry.api.container;

import com.mojang.datafixers.util.Function4;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.BlockEntityPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public abstract class AbstractAlchemistryMenu extends AbstractContainerMenu {

    private final AbstractAlchemistryBlockEntity blockEntity;
    private final Level level;
    private final ContainerData containerData;
    private final int blockEntitySlots;

    protected AbstractAlchemistryMenu(MenuType<?> pMenuType, int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData, int pSlots) {
        super(pMenuType, pContainerId);

        this.containerData = pContainerData;
        this.blockEntitySlots = pSlots;
        blockEntity = ((AbstractAlchemistryBlockEntity) pBlockEntity);
        level = pInventory.player.level;
        addPlayerInventorySlots(pInventory);
        addDataSlots(pContainerData);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        AlchemistryPacketHandler.sendToNear(
                new BlockEntityPacket(this.getBlockEntity().getBlockPos(), this.blockEntity.getUpdateTag()),
                this.level,
                this.blockEntity.getBlockPos(),
                16
        );
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (pIndex < 36) {
            if (!moveItemStackTo(sourceStack, 36, 36 + blockEntitySlots, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < 36 + blockEntitySlots) {
            if (!moveItemStackTo(sourceStack, 0, 36, false))  {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyStack;
    }

    protected <T> void addSlots(Function4<T, Integer, Integer, Integer, Slot> pSlotType, T pContainer, int pRows, int pColumns, int pIndexStart, int pXOrigin, int pYOrigin) {

        for (int row = 0; row < pRows; row++) {
            for (int column = 0; column < pColumns; column++) {
                int slotIndex = column + row * pColumns + pIndexStart;
                int x = pXOrigin + column * 18;
                int y = pYOrigin + row * 18;

                this.addSlot(pSlotType.apply(pContainer, slotIndex, x, y));
            }
        }
    }

    public void addPlayerInventorySlots(Inventory pInventory) {
        // player main inventory
        addSlots(Slot::new, pInventory, 3, 9, 9,8, 86);
        // player hotbar
        addSlots(Slot::new, pInventory, 1, 9, 0,8, 144);
    }

    public AbstractAlchemistryBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ContainerData getContainerData() {
        return containerData;
    }
}
