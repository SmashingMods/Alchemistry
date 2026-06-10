package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlock;
import com.smashingmods.alchemylib.api.blockentity.power.PowerState;
import com.smashingmods.alchemylib.api.blockentity.power.PowerStateProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

public class FusionControllerBlock extends AbstractReactorBlock {

    public FusionControllerBlock(BlockBehaviour.Properties pProperties) {
        super(FusionControllerBlockEntity::new, pProperties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PowerStateProperty.POWER_STATE, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(PowerStateProperty.POWER_STATE, PowerState.DISABLED);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
        pTooltip.add(Component.translatable("tooltip.alchemistry.energy_requirement", Config.Common.fusionEnergyPerTick.get()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            if (pLevel.getBlockState(pPos).getValue(PowerStateProperty.POWER_STATE) != PowerState.DISABLED) {
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                ((ServerPlayer) pPlayer).openMenu((FusionControllerBlockEntity) blockEntity, pPos);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide()) {
            return (level, pos, blockState, blockEntity) -> {
                if (blockEntity instanceof FusionControllerBlockEntity controller) {
                    controller.tick();
                }
            };
        }
        return null;
    }
}
