package com.smashingmods.alchemistry.block.newblocks;

import com.smashingmods.alchemistry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class NewAtomizerBlockEntity extends AbstractNewBlockEntity implements MenuProvider {

    protected final ContainerData data;

    private int progress = 0;
    private int maxProgress = 50;

    public NewAtomizerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Registry.NEW_ATOMIZER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored() & 0xffff;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 1 -> maxProgress = pValue;
                    case 2 -> energyHandler.setEnergy((energyHandler.getEnergyStored() & 0xffff0000) + (pValue & 0xffff));
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return new TranslatableComponent("alchemistry.block.new_atomizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @Nonnull Inventory pInventory, @Nonnull Player pPlayer) {
        return new NewAtomizerMenu(pContainerId, pInventory, this, this.data);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, NewAtomizerBlockEntity pBlockEntity) {
        if (pBlockEntity.progress < pBlockEntity.maxProgress) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
        } else {
            pBlockEntity.progress = 0;
            setChanged(pLevel, pPos, pState);
        }
    }

    @Override
    public LazyOptional<IEnergyStorage> getEnergyHandler() {
        return lazyEnergyHandler.cast();
    }
}
