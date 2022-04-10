package com.smashingmods.alchemistry.blocks.evaporator;

import com.smashingmods.alchemylib.blocks.BaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class EvaporatorBlock extends BaseEntityBlock {
    public EvaporatorBlock() {
        super(Block.Properties.of(Material.METAL).strength(2.0f), EvaporatorTile.class, EvaporatorContainer.class);
    }


    public static final VoxelShape boundingBox = Block.box(1, 1, 1, 15, 12, 15);
    public static final VoxelShape boundingBox2 = Block.box(4, 0.0, 4, 12, 1, 12);
    public static final VoxelShape BOX = Shapes.or(boundingBox, boundingBox2);


    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return BOX;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(stack, getter, tooltips, flag);
        //tooltip.add(new TextComponent(I18n.get("tooltip.alchemistry.evaporator",50)));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, blockState, t) -> {
            if (t instanceof EvaporatorTile) {
                ((EvaporatorTile) t).tickServer();
            }
        };
    }
}