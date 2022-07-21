package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class AbstractProcessingBlockEntity extends BlockEntity implements ProcessingBlockEntity, EnergyBlockEntity, MenuProvider, Nameable {

    private final Component name;
    private int progress = 0;
    private boolean recipeLocked = false;
    private boolean paused = false;

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    public AbstractProcessingBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
        this.name = new TranslatableComponent(String.format("%s.container.%s", Alchemistry.MODID, Objects.requireNonNull(getType().getRegistryName()).getPath()));
    }

    @Override
    public Component getName() {
        return name != null ? name : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    protected Component getDefaultName() {
        return name;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection pConnection, ClientboundBlockEntityDataPacket pPacket) {
        Objects.requireNonNull(pPacket.getTag());
        this.load(pPacket.getTag());
        super.onDataPacket(pConnection, pPacket);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return false;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (!paused) {
                if (!recipeLocked) {
                    updateRecipe();
                }
                if (canProcessRecipe()) {
                    processRecipe();
                }
            }
        }
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public void incrementProgress() {
        this.progress++;
    }

    @Override
    public boolean getRecipeLocked() {
        return this.recipeLocked;
    }

    @Override
    public void setRecipeLocked(boolean pRecipeLocked) {
        this.recipeLocked = pRecipeLocked;
    }

    @Override
    public boolean getPaused() {
        return this.paused;
    }

    @Override
    public void setPaused(boolean pPaused) {
        this.paused = pPaused;
    }

    @Override
    public CustomEnergyStorage getEnergyHandler() {
        return energyHandler;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityEnergy.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }

    @Override
    public void invalidateCaps() {
        lazyEnergyHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("progress", progress);
        pTag.putBoolean("locked", getRecipeLocked());
        pTag.putBoolean("paused", getPaused());
        pTag.put("energy", energyHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        setProgress(pTag.getInt("progress"));
        setRecipeLocked(pTag.getBoolean("locked"));
        setPaused(pTag.getBoolean("paused"));
        energyHandler.deserializeNBT(pTag.get("energy"));
    }
}