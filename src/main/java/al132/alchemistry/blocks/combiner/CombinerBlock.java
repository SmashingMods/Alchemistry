package al132.alchemistry.blocks.combiner;

import al132.alchemistry.Config;
import al132.alchemistry.blocks.BaseTileBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class CombinerBlock extends BaseTileBlock {
    public static final VoxelShape A = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1, 16.0);
    public static final VoxelShape B = Block.makeCuboidShape(2.0, 1.0, 2.0, 14, 11.0, 14);
    public static final VoxelShape C = Block.makeCuboidShape(0.0, 11.0, 0.0, 16.0, 14.0, 16.0);

    public static final VoxelShape BOX = VoxelShapes.or(A, B, C);

    public CombinerBlock() {
        super("chemical_combiner", CombinerTile::new, Block.Properties.create(Material.IRON));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOX;//super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOX;//super.getShape(state, worldIn, pos, context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(I18n.format("tooltip.alchemistry.energy_requirement", Config.COMBINER_ENERGY_PER_TICK.get())));
    }
}
