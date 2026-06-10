package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.block.TooltipBlock;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
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
import javax.annotation.Nonnull;

import java.util.function.Consumer;

public class CombinerBlock extends AbstractProcessingBlock implements TooltipBlock {

    public CombinerBlock(BlockBehaviour.Properties pProperties) {
        super(CombinerBlockEntity::new, pProperties);
    }

    public static final VoxelShape A = Block.box(0.0, 0.0, 0.0, 16.0, 1, 16.0);
    public static final VoxelShape B = Block.box(2.0, 1.0, 2.0, 14, 11.0, 14);
    public static final VoxelShape C = Block.box(0.0, 11.0, 0.0, 16.0, 14.0, 16.0);

    public static final VoxelShape SHAPE = Shapes.or(A, B, C);

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState pState) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipAdder, TooltipFlag pFlag) {
        pTooltipAdder.accept(MutableComponent.create(new TranslatableContents("tooltip.alchemistry.energy_requirement", String.valueOf(Config.Common.combinerEnergyPerTick.get()), TranslatableContents.NO_ARGS)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            ((ServerPlayer) pPlayer).openMenu((CombinerBlockEntity) blockEntity, pPos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pos, blockState, blockEntity) -> {
                if (blockEntity instanceof CombinerBlockEntity) {
                    ((CombinerBlockEntity) blockEntity).tick();
                }
            };
        }
        return null;
    }
}
