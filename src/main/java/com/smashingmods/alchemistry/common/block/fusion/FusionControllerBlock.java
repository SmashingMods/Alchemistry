package com.smashingmods.alchemistry.common.block.fusion;

import com.mojang.serialization.MapCodec;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlock;
import com.smashingmods.alchemylib.api.blockentity.power.PowerState;
import com.smashingmods.alchemylib.api.blockentity.power.PowerStateProperty;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FusionControllerBlock extends AbstractReactorBlock {

    public static final MapCodec<FusionControllerBlock> CODEC = simpleCodec(FusionControllerBlock::new);

    public FusionControllerBlock() {
        super(FusionControllerBlockEntity::new);
    }

    private FusionControllerBlock(BlockBehaviour.Properties pProperties) {
        this();
    }

    @Override
    public MapCodec<FusionControllerBlock> codec() {
        return CODEC;
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
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.isSpectator()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {
            if (pLevel.getBlockState(pPos).getValue(PowerStateProperty.POWER_STATE) != PowerState.DISABLED) {
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if (blockEntity instanceof FusionControllerBlockEntity fusionControllerBlockEntity) {
                    serverPlayer.openMenu(fusionControllerBlockEntity, buf -> buf.writeBlockPos(pPos));
                }
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.FAIL;
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
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

