package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.block.AbstractAlchemistryBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import java.util.List;

public class CombinerBlock extends AbstractAlchemistryBlock {

    public CombinerBlock() {
        super(CombinerBlockEntity::new);
    }

    public static final VoxelShape A = Block.box(0.0, 0.0, 0.0, 16.0, 1, 16.0);
    public static final VoxelShape B = Block.box(2.0, 1.0, 2.0, 14, 11.0, 14);
    public static final VoxelShape C = Block.box(0.0, 11.0, 0.0, 16.0, 14.0, 16.0);

    public static final VoxelShape SHAPE = Shapes.or(A, B, C);

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
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable BlockGetter pLevel, @Nonnull List<Component> pTooltip, @Nonnull TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(new TranslatableComponent("tooltip.alchemistry.energy_requirement", Config.Common.combinerEnergyPerTick.get()));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState pState, Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            NetworkHooks.openGui(((ServerPlayer) pPlayer), (CombinerBlockEntity) blockEntity, pPos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pos, blockState, blockEntity) -> {
                if (blockEntity instanceof CombinerBlockEntity) {
                    ((CombinerBlockEntity) blockEntity).tick(pLevel);
                }
            };
        }
        return null;
    }
}
