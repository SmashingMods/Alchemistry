package com.smashingmods.alchemistry.blocks.fission;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.blocks.PowerStatus;
import com.smashingmods.alchemylib.blocks.BaseEntityBlock;
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

import javax.annotation.Nullable;
import java.util.List;

public class FissionControllerBlock extends BaseEntityBlock {
    public static final EnumProperty<PowerStatus> STATUS = EnumProperty.create("status", PowerStatus.class, PowerStatus.values());
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


    public FissionControllerBlock() {
        super(Block.Properties.of(Material.METAL).strength(2.0f), FissionTile.class, FissionContainer.class);
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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(stack, getter, tooltips, flag);
        tooltips.add(new TextComponent(I18n.get("tooltip.alchemistry.energy_requirement", Config.FISSION_ENERGY_PER_TICK.get())));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof FissionTile) {
                ((FissionTile) t).tickServer();
            }
        };
    }
}
