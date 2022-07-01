package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.PowerState;
import com.smashingmods.alchemistry.api.blockentity.PowerStateProperty;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Function;

public class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Alchemistry.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile atomizerModel = new ModelFile.ExistingModelFile(modLoc("block/atomizer"), models().existingFileHelper);
        ModelFile combinerModel = new ModelFile.ExistingModelFile(modLoc("block/combiner"), models().existingFileHelper);
        ModelFile dissolverModel = new ModelFile.ExistingModelFile(modLoc("block/dissolver"), models().existingFileHelper);
        ModelFile evaporatorModel = new ModelFile.ExistingModelFile(modLoc("block/evaporator"), models().existingFileHelper);
        ModelFile liquifierModel = new ModelFile.ExistingModelFile(modLoc("block/liquifier"), models().existingFileHelper);

        registerMachineModel(BlockRegistry.ATOMIZER, atomizerModel);
        registerMachineModel(BlockRegistry.COMBINER, combinerModel);
        registerMachineModel(BlockRegistry.DISSOLVER, dissolverModel);
        registerMachineModel(BlockRegistry.EVAPORTOR, evaporatorModel);
        registerMachineModel(BlockRegistry.LIQUIFIER, liquifierModel);

        registerAxisBlock(BlockRegistry.FISSION_CORE);
        registerAxisBlock(BlockRegistry.FUSION_CORE);
        registerSimpleBlock(BlockRegistry.REACTOR_CASING);
        registerController(BlockRegistry.FISSION_CONTROLLER);
        registerController(BlockRegistry.FUSION_CONTROLLER);
    }

    private void registerSimpleBlock(RegistryObject<Block> pBlock) {
        simpleBlock(pBlock.get());
        registerBlockItemModel(pBlock);
    }

    private void registerAxisBlock(RegistryObject<RotatedPillarBlock> pBlock) {
        axisBlock(pBlock.get());
        registerBlockItemModel(pBlock);
    }

    private <T extends Block> void registerBlockItemModel(RegistryObject<T> pBlock) {
        itemModels().withExistingParent(pBlock.getId().getPath(), modLoc(String.format("block/%s", pBlock.getId().getPath())));
    }

    private void registerMachineModel(RegistryObject<Block> pBlock, ModelFile pModelFile) {
        getVariantBuilder(pBlock.get()).forAllStates(blockState -> {
            Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(pModelFile)
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -19 : 0)
                    .rotationY(direction.getAxis() == Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
        registerBlockItemModel(pBlock);
    }

    private void registerController(RegistryObject<Block> pBlock) {

        Block controller = pBlock.get();
        Objects.requireNonNull(controller.getRegistryName());
        String type = controller.getRegistryName().getPath().split("_")[0];

        ResourceLocation side = modLoc(String.format("block/%s_controller_side", type));
        ResourceLocation end = modLoc(String.format("block/%s_controller_end", type));
        ResourceLocation off = modLoc(String.format("block/%s_controller_off", type));
        ResourceLocation standby = modLoc(String.format("block/%s_controller_standby", type));
        ResourceLocation on = modLoc(String.format("block/%s_controller_on", type));

        BlockModelBuilder controllerOff = models().cube(String.format("%s_controller_off", type), end, end, off, end, side, side);
        BlockModelBuilder controllerStandby = models().cube(String.format("%s_controller_standby", type), end, end, standby, end, side, side);
        BlockModelBuilder controllerOn = models().cube(String.format("%s_controller_on", type), end, end, on, end, side, side);

        getVariantBuilder(controller).forAllStates(blockState -> {
            Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

            Function<BlockState, ModelFile> modelFunction = state -> {
                PowerState status = blockState.getValue(PowerStateProperty.STATUS);
                return switch (status) {
                    case OFF -> controllerOff;
                    case STANDBY -> controllerStandby;
                    case ON -> controllerOn;
                };
            };

            return ConfiguredModel.builder()
                    .modelFile(modelFunction.apply(blockState))
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0)
                    .rotationY(direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
        // registerBlockItemModel(pBlock);
    }

}
