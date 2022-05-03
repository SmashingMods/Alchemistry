package com.smashingmods.alchemistry.common.block.dissolver;

import com.smashingmods.alchemistry.api.block.AbstractAlchemistryBlock;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class DissolverBlock extends AbstractAlchemistryBlock {

    public DissolverBlock() {
        super(DissolverBlockEntity::new);
    }

    public static final VoxelShape A = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    public static final VoxelShape B = Block.box(2.0, 4.0, 2.0, 14, 14.0, 14);
    public static final VoxelShape SHAPE = Shapes.or(A,B);

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return SHAPE;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState pState, Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            NetworkHooks.openGui(((ServerPlayer) pPlayer), (DissolverBlockEntity) blockEntity, pPos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pos, blockState, blockEntity) -> {
                if (blockEntity instanceof DissolverBlockEntity) {
                    ((DissolverBlockEntity) blockEntity).tick(pLevel);
                }
            };
        }
        return null;
    }
}
