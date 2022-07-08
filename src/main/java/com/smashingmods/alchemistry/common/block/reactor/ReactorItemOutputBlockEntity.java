package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemistry.api.blockentity.handler.ModItemStackHandler;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ReactorItemOutputBlockEntity extends BlockEntity {

    private ModItemStackHandler outputHandler = new ModItemStackHandler(this, 0);
    private final LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);

    public ReactorItemOutputBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.REACTOR_ITEM_OUTPUT_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public void setOutputHandler(ModItemStackHandler pOutputHandler) {
        this.outputHandler = pOutputHandler;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyOutputHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }
}
