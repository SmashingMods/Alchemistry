package al132.alchemistry.blocks.liquifier;

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

public class LiquifierBlock extends ABaseTileBlock {

    public static final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
    public static final VoxelShape rest = Block.box(2, 1, 2, 14, 16, 14);
    public static final VoxelShape BOX = Shapes.or(base, rest);

    public LiquifierBlock(){
        super(Block.Properties.of(Material.METAL).strength(2.0f), LiquifierTile.class, LiquifierContainer.class);
    }


    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return BOX;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(stack, getter, tooltips, flag);
        tooltips.add(new TextComponent(I18n.get("tooltip.alchemistry.energy_requirement", Config.LIQUIFIER_ENERGY_PER_TICK.get())));
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof LiquifierTile) {
                ((LiquifierTile) t).tickServer();
            }
        };
    }
}
