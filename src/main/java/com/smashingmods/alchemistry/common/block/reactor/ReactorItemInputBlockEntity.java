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

public class ReactorItemInputBlockEntity extends BlockEntity {

    private ModItemStackHandler inputHandler = new ModItemStackHandler(this, 0);
    private final LazyOptional<IItemHandler> lazyInputHandler = LazyOptional.of(() -> inputHandler);

    public ReactorItemInputBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.REACTOR_ITEM_INPUT_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public void setInputHandler(ModItemStackHandler pInputHandler) {
        this.inputHandler = pInputHandler;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyInputHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }
}
