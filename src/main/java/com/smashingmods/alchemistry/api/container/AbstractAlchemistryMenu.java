package com.smashingmods.alchemistry.api.container;

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
    private static final int BlockEntitySlots = 1;

    protected AbstractAlchemistryMenu(MenuType<?> pMenuType, int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(pMenuType, pContainerId);

        this.containerData = pContainerData;
        blockEntity = ((AbstractAlchemistryBlockEntity) pBlockEntity);
        level = pInventory.player.level;

        addPlayerInventory(pInventory);
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
            if (!moveItemStackTo(sourceStack, 36, 36 + BlockEntitySlots, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < 36 + BlockEntitySlots) {
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

    private void addPlayerInventory(Inventory pInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {

                int index = column + row * 9 + 9;
                int x = 8 + column * 18;
                int y = 86 + row * 18;

                this.addSlot(new Slot(pInventory, index, x, y));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(pInventory, column, 8 + column * 18, 144));
        }
    }

    public AbstractAlchemistryBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ContainerData getContainerData() {
        return containerData;
    }
}
