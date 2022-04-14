package com.smashingmods.alchemistry.blocks.dissolver;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.BaseEntityBlock;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DissolverBlock extends BaseEntityBlock {
    public static final VoxelShape A = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    public static final VoxelShape B = Block.box(2.0, 4.0, 2.0, 14, 14.0, 14);
    public static final VoxelShape BOX = Shapes.or(A,B);
    public DissolverBlock() {
        super(Block.Properties.of(Material.METAL).strength(2.0f), DissolverBlockEntity::new, DissolverContainer::new);
    }


    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(@Nonnull BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
        return BOX;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter getter, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, getter, tooltips, flag);
        tooltips.add(new TextComponent(I18n.get("tooltip.alchemistry.energy_requirement", Config.DISSOLVER_ENERGY_PER_TICK.get())));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof DissolverBlockEntity) {
                ((DissolverBlockEntity) t).tickServer();
            }
        };
    }
}