package al132.alchemistry.blocks.combiner;

import al132.alchemistry.Config;
import al132.alib.blocks.ABaseTileBlock;
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

import javax.annotation.Nullable;
import java.util.List;

public class CombinerBlock extends ABaseTileBlock {
    public static final VoxelShape A = Block.box(0.0, 0.0, 0.0, 16.0, 1, 16.0);
    public static final VoxelShape B = Block.box(2.0, 1.0, 2.0, 14, 11.0, 14);
    public static final VoxelShape C = Block.box(0.0, 11.0, 0.0, 16.0, 14.0, 16.0);

    public static final VoxelShape BOX = Shapes.or(A, B, C);

    public CombinerBlock() {
        super(Block.Properties.of(Material.METAL).strength(2.0f), CombinerTile.class, CombinerContainer.class);
    }


    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return BOX;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(stack, getter, tooltips, flag);
        tooltips.add(new TextComponent(I18n.get("tooltip.alchemistry.energy_requirement", Config.COMBINER_ENERGY_PER_TICK.get())));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof CombinerTile) {
                ((CombinerTile) t).tickServer();
            }
        };
    }
}