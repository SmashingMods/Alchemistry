package al132.alchemistry.blocks.atomizer;

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

public class AtomizerBlock extends BaseTileBlock {
    public AtomizerBlock() {
        super("atomizer", AtomizerTile::new, Properties.create(Material.IRON));
    }

    public static final VoxelShape base = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
    public static final VoxelShape rest = Block.makeCuboidShape(2, 1, 2, 14, 16, 14);
    public static final VoxelShape BOX = VoxelShapes.or(base, rest);


    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOX;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOX;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(I18n.format("tooltip.alchemistry.energy_requirement", Config.ATOMIZER_ENERGY_PER_TICK.get())));
    }
}
