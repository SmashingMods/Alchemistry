package com.smashingmods.alchemistry.block;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.*;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AlchemistryBlockEntity extends BlockEntity implements Nameable, InventoryBlockEntity, GuiBlockEntity {

    private int notifyTicks = 0;
    private int dirtyTicks = 0;

    Component name;

    protected CustomStackHandler inputHandler;
    protected CustomStackHandler outputHandler;

    public IEnergyStorage energy;

    private CombinedInvWrapper combinedInvWrapper;
    protected LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> combinedInvWrapper);

    public AlchemistryBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pBlockPos, BlockState pBlockState) {
        super(pBlockEntityType, pBlockPos, pBlockState);

        AutomationStackHandler automationInputHandler = getAutomationInputHandler(getInputHandler());
        AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(getOutputHandler());

        combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);

        if (this instanceof EnergyBlockEntity) {
            energy = ((EnergyBlockEntity) this).getEnergy();
        }
    }

    @Override
    @Nonnull
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return this.getName();
    }

    protected Component getDefaultName() {
        return new TranslatableComponent(String.format("%s.container.%s", Alchemistry.MODID, Objects.requireNonNull(getType().getRegistryName()).getPath()));
    }

    @Override
    public int getHeight() {
        return 222;
    }

    @Override
    public int getWidth() {
        return 174;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY && this instanceof EnergyBlockEntity) {
            return LazyOptional.of(() -> energy).cast();
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return ((InventoryBlockEntity) this).getExternalInventory().cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this instanceof FluidBlockEntity) {
            return ((FluidBlockEntity) this).getFluidHandler().cast();
        } else return super.getCapability(cap, side);
    }

    @Override
    public AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable input) {
        return new AutomationStackHandler(input) {
            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable output) {
        return new AutomationStackHandler(output) {
            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!getStackInSlot(slot).isEmpty()) return super.extractItem(slot, amount, simulate);
                else return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        getInputHandler().deserializeNBT(pTag.getCompound("input"));
        getOutputHandler().deserializeNBT(pTag.getCompound("output"));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("input", getInputHandler().serializeNBT());
        pTag.put("output", getOutputHandler().serializeNBT());
    }

    @Override
    public LazyOptional<IItemHandler> getExternalInventory() {
        return itemHandler;
    }

    @Override
    public CombinedInvWrapper getAutomationInventory() {
        return combinedInvWrapper;
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        saveAdditional(updateTag);
        return updateTag;
    }

    @Override
    public void onDataPacket(Connection pConnection, ClientboundBlockEntityDataPacket pDataPacket) {
        this.load(Objects.requireNonNull(pDataPacket.getTag()));
        super.onDataPacket(pConnection, pDataPacket);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
        AtomicBoolean didInteract = new AtomicBoolean(false);
        if (this instanceof FluidBlockEntity) {
            ItemStack heldItem = pPlayer.getItemInHand(pHand);
            heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                didInteract.set(FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pPos, null));
                this.setChanged();
            });
        }
        return didInteract.get();
    }

    /**
     * For reference to block state update flags used in this class.
     *
     * Sets a block state into this world.Flags are as follows:
     * 1 will cause a block update.
     * 2 will send the change to clients.
     * 4 will prevent the block from being re-rendered.
     * 8 will force any re-renders to run on the main thread instead
     * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
     * 32 will prevent neighbor reactions from spawning drops.
     * 64 will signify the block is being moved.
     * Flags can be OR-ed
     */

    public void updateGUIEvery(int pTicks) {
        this.notifyTicks++;
        if (this.notifyTicks >= pTicks) {
            if (this.level != null) {
                BlockState state = this.getBlockState();
                this.level.sendBlockUpdated(getBlockPos(), state, state, 22);
            }
            this.notifyTicks = 0;
        }
    }

    @SuppressWarnings("unused")
    public void updateGUI() {
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(getBlockPos(), state, state, 22);
        }
    }

    @SuppressWarnings("unused")
    public void markDirtyGUIEvery(int ticks) {
        this.dirtyTicks++;
        if (this.dirtyTicks >= ticks) {
            this.setChanged();
            this.dirtyTicks = 0;
        }
    }
}
