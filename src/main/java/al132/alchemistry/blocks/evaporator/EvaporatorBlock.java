package al132.alchemistry.blocks.evaporator;

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

public class EvaporatorBlock extends BaseTileBlock {
    public EvaporatorBlock() {
        super("evaporator", EvaporatorTile::new, Block.Properties.create(Material.IRON));
    }


    public static final VoxelShape boundingBox = Block.makeCuboidShape(1, 1, 1, 15, 12, 15);
    public static final VoxelShape boundingBox2 = Block.makeCuboidShape(4, 0.0, 4, 12, 1, 12);
    public static final VoxelShape BOX = VoxelShapes.or(boundingBox, boundingBox2);


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
        tooltip.add(new StringTextComponent(I18n.format("tooltip.alchemistry.evaporator",50)));
    }
}