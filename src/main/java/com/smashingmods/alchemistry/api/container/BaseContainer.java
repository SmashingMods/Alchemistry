package com.smashingmods.alchemistry.api.container;

import com.smashingmods.alchemistry.api.blockentity.GuiBlockEntity;
import com.smashingmods.alchemistry.blocks.AlchemistryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

import static net.minecraftforge.energy.CapabilityEnergy.ENERGY;

public abstract class BaseContainer extends AbstractContainerMenu {

    public AlchemistryBlockEntity blockEntity;
    private final Inventory inventory;
    public final int SLOT_COUNT;

    public BaseContainer(MenuType<?> pMenuType, int pID, BlockPos pBlockPos, Inventory pInventory, int pSlotCount) {
        //MenuType<?> p_39229_, int p_39230_, Inventory p_39231_, Container p_39232_, int p_39233_
        super(pMenuType, pID);

        this.blockEntity = (AlchemistryBlockEntity) pInventory.player.getLevel().getBlockEntity(pBlockPos);
        //this.player = player;
        this.inventory = pInventory;
        this.SLOT_COUNT = pSlotCount;
        /*if (tile instanceof EnergyTile) trackInt(new DataSlot() {

            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                ((CustomEnergyStorage) tile.energy).setEnergy(value);
            }

        });
        if (tile instanceof FluidTile) trackInt(new DataSlot() {
            @Override
            public int get() {
                return ((FluidTile) tile).getFluidHandler().orElse(null).getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int value) {
                FluidStack x = ((FluidTile) tile).getFluidHandler().orElse(null)
                        .getFluidInTank(0)
                        .copy();
                x.setAmount(value);
                ((FluidTank) ((FluidTile) tile).getFluidHandler().orElse(null)).setFluid(x);
            }
        });
        */
    }

    public void trackInt(DataSlot pDataSlot) {

        addDataSlot(new DataSlot() {
            private int lastKnown;

            @Override
            public int get() {
                return pDataSlot.get() & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = pDataSlot.get();
                pDataSlot.set((full & 0xffff0000) | (val & 0xffff));
            }

            @Override
            public boolean checkAndClearUpdateFlag() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });

        addDataSlot(new DataSlot() {
            private int lastKnown;

            @Override
            public int get() {
                return (pDataSlot.get() >> 16) & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = pDataSlot.get();
                pDataSlot.set((full & 0x0000ffff) | ((val & 0xffff) << 16));
            }

            @Override
            public boolean checkAndClearUpdateFlag() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });
    }

    public int getEnergy() {
        return blockEntity.getCapability(ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getFluid() {
        return blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .map(handler -> handler.getFluidInTank(0).getAmount()).orElse(0);
    }

    public int getFluidCapacity() {
        return blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .map(handler -> handler.getTankCapacity(0)).orElse(0);
    }

    public int getEnergyCapacity() {
        return blockEntity.getCapability(ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    public void addSlotArray(int pStartIndex, int pX, int pY, int pRows, int pColumns, IItemHandler pItemhandler) {
        int x = pX;
        int y = pY;
        int index = pStartIndex;

        for (int row = 1; row <= pRows; row++) {
            for (int column = 1; column <= pColumns; column++) {
                this.addSlot(new SlotItemHandler(pItemhandler, index, x, y));
                x += 18;
                index++;
            }
            x = pX;
            y += 18;
        }
    }

    public void addPlayerSlots() {
        for (int row = 0; row <= 2; row++) {
            for (int col = 0; col <= 8; col++) {
                int x = 8 + col * 18;
                int y = row * 18 + ((GuiBlockEntity) blockEntity).getHeight() - 82;
                this.addSlot(new Slot(inventory, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row <= 8; row++) {
            int x = 8 + row * 18;
            int y = ((GuiBlockEntity) blockEntity).getHeight() - 24;
            this.addSlot(new Slot(inventory, row, x, y));
        }
    }

/*
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < slotCount) {
                if (!this.mergeItemStack(itemstack1, slotCount, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, slotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return itemstack;
    }*/

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player pPlayer, int pIndex) {
        ItemStack itemStackToReturn = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemStackFromSlot = slot.getItem();
            itemStackToReturn = itemStackFromSlot.copy();

            if (pIndex < SLOT_COUNT) {
                if (!this.moveItemStackTo(itemStackFromSlot, SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStackFromSlot, 0, SLOT_COUNT, false)) return ItemStack.EMPTY;

            if (itemStackFromSlot.getCount() <= 0) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStackToReturn;
    }
}
