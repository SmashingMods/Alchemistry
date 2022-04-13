package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseBlockEntity extends BlockEntity implements Nameable {

    private int notifyTicks = 0;

    public IEnergyStorage energy;
    public LazyOptional<IEnergyStorage> energyHolder;

    public BaseBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pPos, BlockState pState) {
        super(pBlockEntityType, pPos, pState);

        if (this instanceof EnergyBlockEntity) {
            energy = ((EnergyBlockEntity) this).initEnergy();
            energyHolder = LazyOptional.of(() -> energy);
        }
    }

    public boolean onBlockActivated(BlockState pBlockState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pBlockHitResult) {
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

    //TODO: why is max never used?
    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);

        if (this instanceof EnergyBlockEntity) {
            @SuppressWarnings("unused")
            int max = ((EnergyBlockEntity) this).getEnergy().getMaxEnergyStored();
            this.energy = ((EnergyBlockEntity) this).initEnergy();
            if (energy instanceof CustomEnergyStorage) {
                ((CustomEnergyStorage) energy).receiveEnergyInternal(pTag.getInt("energy"), false);
            } else {
                energy.receiveEnergy(pTag.getInt("energy"), false);
            }
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this instanceof EnergyBlockEntity) {
            pTag.putInt("energy", energy.getEnergyStored());
        }
        //return super.save(compound);
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY && this instanceof EnergyBlockEntity) {
            return this.energyHolder.cast();
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this instanceof InventoryBlockEntity) {
            return ((InventoryBlockEntity) this).getExternalInventory().cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this instanceof FluidBlockEntity) {
            return ((FluidBlockEntity) this).getFluidHandler().cast();
        } else return super.getCapability(cap, side);
    }

    @Override
    @Nonnull
    public TextComponent getDisplayName() {
        //TODO make more localization friendly
        String regName = Objects.requireNonNull(getType().getRegistryName()).getPath().replaceAll("_", " ");
        return new TextComponent(regName.toUpperCase());
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

    @SuppressWarnings("unused")
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
}