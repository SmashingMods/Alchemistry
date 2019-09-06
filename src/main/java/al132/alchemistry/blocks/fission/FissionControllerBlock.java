package al132.alchemistry.blocks.fission;

import al132.alchemistry.Config;
import al132.alchemistry.blocks.BaseTileBlock;
import al132.alchemistry.blocks.PowerStatus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class FissionControllerBlock extends BaseTileBlock {
    public static final EnumProperty<PowerStatus> STATUS = EnumProperty.create("status", PowerStatus.class, PowerStatus.values());
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


    public FissionControllerBlock() {
        super("fission_controller", FissionTile::new, Block.Properties.create(Material.IRON));
        this.setDefaultState(this.getDefaultState().with(STATUS, PowerStatus.OFF).with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STATUS, FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState()
                .with(FACING, context.getPlacementHorizontalFacing().getOpposite())
                .with(STATUS, PowerStatus.OFF);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(I18n.format("tooltip.alchemistry.energy_requirement", Config.FISSION_ENERGY_PER_TICK.get())));
    }


}
