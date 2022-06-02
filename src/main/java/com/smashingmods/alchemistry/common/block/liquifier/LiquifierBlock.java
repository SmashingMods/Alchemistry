package com.smashingmods.alchemistry.common.block.liquifier;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.smashingmods.alchemistry.api.block.AbstractAlchemistryBlock;
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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LiquifierBlock extends AbstractAlchemistryBlock {

    public LiquifierBlock() {
        super(LiquifierBlockEntity::new);
    }

    public static final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
    public static final VoxelShape rest = Block.box(2, 1, 2, 14, 16, 14);
    public static final VoxelShape SHAPE = Shapes.or(base, rest);

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(@Nonnull BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            boolean interactionSuccessful = true;

            if (blockEntity instanceof LiquifierBlockEntity) {
                interactionSuccessful = ((LiquifierBlockEntity) blockEntity).onBlockActivated(pLevel, pPos, pPlayer, pHand);
            }

            if (!interactionSuccessful) {
                NetworkHooks.openGui(((ServerPlayer) pPlayer), (LiquifierBlockEntity) blockEntity, pPos);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pBlockPos, pBlockState, pBlockEntity) -> {
                if (pBlockEntity instanceof LiquifierBlockEntity blockEntity) {
                    blockEntity.tick(pLevel);
                }
            };
        }
        return null;
    }
}
