package com.smashingmods.alchemistry.common.block.atomizer;

import com.mojang.serialization.MapCodec;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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

    public static final MapCodec<AtomizerBlock> CODEC = simpleCodec(AtomizerBlock::new);

    public AtomizerBlock() {
        super(AtomizerBlockEntity::new);
    }

    private AtomizerBlock(BlockBehaviour.Properties pProperties) {
        this();
    }

    public static final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
    public static final VoxelShape rest = Block.box(2, 1, 2, 14, 16, 14);
    public static final VoxelShape SHAPE = Shapes.or(base, rest);

    @Override
    public MapCodec<AtomizerBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.alchemistry.energy_requirement", Config.Common.atomizerEnergyPerTick.get()));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            boolean interactionSuccessful = true;

            if (blockEntity instanceof AtomizerBlockEntity) {
                interactionSuccessful = ((AtomizerBlockEntity) blockEntity).onBlockActivated(level, pos, player, hand);
            }

            if (!interactionSuccessful && blockEntity instanceof AtomizerBlockEntity atomizerBlockEntity) {
                serverPlayer.openMenu(atomizerBlockEntity, buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.SUCCESS;
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

