package com.smashingmods.alchemistry.common.block.liquifier;

import com.mojang.serialization.MapCodec;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class LiquifierBlock extends AbstractProcessingBlock {

    public static final MapCodec<LiquifierBlock> CODEC = simpleCodec(LiquifierBlock::new);

    public LiquifierBlock() {
        super(LiquifierBlockEntity::new);
    }

    private LiquifierBlock(BlockBehaviour.Properties pProperties) {
        this();
    }

    public static final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
    public static final VoxelShape rest = Block.box(2, 1, 2, 14, 16, 14);
    public static final VoxelShape SHAPE = Shapes.or(base, rest);

    @Override
    public MapCodec<LiquifierBlock> codec() {
        return CODEC;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.isSpectator()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            boolean interactionSuccessful = true;

            if (blockEntity instanceof LiquifierBlockEntity) {
                interactionSuccessful = ((LiquifierBlockEntity) blockEntity).onBlockActivated(pLevel, pPos, pPlayer, pHand);
            }

            if (!interactionSuccessful && blockEntity instanceof LiquifierBlockEntity liquifierBlockEntity) {
                serverPlayer.openMenu(liquifierBlockEntity, buf -> buf.writeBlockPos(pPos));
            }
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pBlockPos, pBlockState, pBlockEntity) -> {
                if (pBlockEntity instanceof LiquifierBlockEntity blockEntity) {
                    blockEntity.tick();
                }
            };
        }
        return null;
    }
}
