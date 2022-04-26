package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class AbstractAlchemistryBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    Component name;

    public AbstractAlchemistryBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
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
    @Nonnull
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

    public void getDrops() {
        if (this instanceof InventoryBlockEntity) {
            CombinedInvWrapper handler = ((InventoryBlockEntity) this).getAutomationInventory();
            SimpleContainer container = new SimpleContainer(handler.getSlots());
            for (int i = 0; i < handler.getSlots(); i++) {
                container.setItem(i, handler.getStackInSlot(i));
            }
            Containers.dropContents(Objects.requireNonNull(this.level), this.worldPosition, container);
        }
    }
}