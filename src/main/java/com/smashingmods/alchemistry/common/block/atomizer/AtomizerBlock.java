package com.smashingmods.alchemistry.common.block.atomizer;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

import java.util.List;

public class AtomizerBlock extends AbstractProcessingBlock {

    public AtomizerBlock(BlockBehaviour.Properties pProperties) {
        super(AtomizerBlockEntity::new, pProperties);
    }

    public static final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
    public static final VoxelShape rest = Block.box(2, 1, 2, 14, 16, 14);
    public static final VoxelShape SHAPE = Shapes.or(base, rest);

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
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
        pTooltip.add(MutableComponent.create(new TranslatableContents("tooltip.alchemistry.energy_requirement", String.valueOf(Config.Common.atomizerEnergyPerTick.get()), TranslatableContents.NO_ARGS)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            if (pLevel.getBlockEntity(pPos) instanceof AtomizerBlockEntity blockEntity && blockEntity.onBlockActivated(pLevel, pPos, pPlayer, pHand)) {
                return InteractionResult.CONSUME;
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            if (pLevel.getBlockEntity(pPos) instanceof AtomizerBlockEntity blockEntity) {
                ((ServerPlayer) pPlayer).openMenu(blockEntity, pPos);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pBlockPos, pBlockState, pBlockEntity) -> {
                if (pBlockEntity instanceof AtomizerBlockEntity blockEntity) {
                    blockEntity.tick();
                }
            };
        }
        return null;
    }
}
