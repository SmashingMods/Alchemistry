package com.smashingmods.alchemistry.block.fusion;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.BaseEntityBlock;
import com.smashingmods.alchemistry.block.PowerStatus;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FusionControllerBlock extends BaseEntityBlock {
    public static final EnumProperty<PowerStatus> STATUS = EnumProperty.create("status", PowerStatus.class, PowerStatus.values());
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public FusionControllerBlock() {
        super(Block.Properties.of(Material.METAL).strength(2.0f), FusionBlockEntity::new, FusionContainer::new);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(STATUS, PowerStatus.OFF)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATUS, FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(STATUS, PowerStatus.OFF);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter getter, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, getter, tooltips, flag);
        tooltips.add(new TextComponent(I18n.get("tooltip.alchemistry.energy_requirement", Config.FUSION_ENERGY_PER_TICK.get())));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) return null;
        return (lvl, pos, blockState, t) -> {
            if (t instanceof FusionBlockEntity) {
                ((FusionBlockEntity) t).tickServer();
            }
        };
    }
}
