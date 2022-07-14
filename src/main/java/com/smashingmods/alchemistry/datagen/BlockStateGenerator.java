package com.smashingmods.alchemistry.datagen;

import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
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
        ModelFile liquifierModel = new ModelFile.ExistingModelFile(modLoc("block/liquifier"), models().existingFileHelper);

        registerMachineModel(BlockRegistry.ATOMIZER, atomizerModel);
        registerMachineModel(BlockRegistry.COMBINER, combinerModel);
        registerMachineModel(BlockRegistry.DISSOLVER, dissolverModel);
        registerMachineModel(BlockRegistry.LIQUIFIER, liquifierModel);

        registerControllerModel(BlockRegistry.FISSION_CONTROLLER);
        registerControllerModel(BlockRegistry.FUSION_CONTROLLER);
        registerAxisBlock(BlockRegistry.FISSION_CORE);
        registerAxisBlock(BlockRegistry.FUSION_CORE);
        registerSimpleBlock(BlockRegistry.REACTOR_CASING);
        registerSimpleBlock(BlockRegistry.REACTOR_GLASS);
        registerReactorIOModels();
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

    private void registerControllerModel(RegistryObject<Block> pBlock) {

        Block controller = pBlock.get();
        Objects.requireNonNull(controller.getRegistryName());

        Function5<PowerState, ResourceLocation, ResourceLocation, ResourceLocation, ResourceLocation, BlockModelBuilder> modelFunction = (state, face, back, hSide, vSide) -> {
            String faceString = String.format("%s_%s", face.getPath(), state.getSerializedName());
            return models().withExistingParent(faceString, mcLoc("block/cube"))
                    .texture("particle", hSide)
                    .texture("down", vSide)
                    .texture("up", vSide)
                    .texture("north", modLoc(String.format("block/%s", faceString)))
                    .texture("south", back)
                    .texture("east", hSide)
                    .texture("west", hSide);
        };

        getVariantBuilder(controller).forAllStates(blockState -> {
            Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            String type = controller.getRegistryName().getPath().split("_")[0];
            ResourceLocation side = modLoc(String.format("block/%s_chamber_controller_side", type));
            ResourceLocation end = modLoc(String.format("block/%s_chamber_controller_end", type));
            return ConfiguredModel.builder()
                    .modelFile(modelFunction.apply(blockState.getValue(PowerStateProperty.POWER_STATE), controller.getRegistryName(), side, end, side))
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0)
                    .rotationY(direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
        itemModels().withExistingParent(pBlock.getId().getPath(), modLoc(String.format("block/%s_standby", pBlock.getId().getPath())));
    }

    private void registerReactorIOModels() {
        ResourceLocation side = modLoc("block/reactor_casing");
        ResourceLocation input = modLoc("block/reactor_item_input");
        ResourceLocation output = modLoc("block/reactor_item_output");
        ResourceLocation energy = modLoc("block/reactor_energy_input");

        registerFaceBlock(BlockRegistry.REACTOR_ITEM_INPUT, input, side);
        registerFaceBlock(BlockRegistry.REACTOR_ITEM_OUTPUT, output, side);
        registerPoweredFaceBlock(BlockRegistry.REACTOR_ENERGY_INPUT, energy, side);
    }

    private void registerFaceBlock(RegistryObject<Block> pBlock, ResourceLocation pFace, ResourceLocation pSide) {
        String path = pBlock.getId().getPath();
        getVariantBuilder(pBlock.get()).forAllStates(blockState -> {
            Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
            ModelFile modelFile = models().withExistingParent(path, mcLoc("block/cube"))
                    .texture("particle", pSide)
                    .texture("down", pSide)
                    .texture("up", pSide)
                    .texture("north", pFace)
                    .texture("south", pSide)
                    .texture("east", pSide)
                    .texture("west", pSide);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0)
                    .rotationY(direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
        itemModels().withExistingParent(path, modLoc(String.format("block/%s", path)));
    }

    @SuppressWarnings("SameParameterValue")
    private void registerPoweredFaceBlock(RegistryObject<Block> pBlock, ResourceLocation pFace, ResourceLocation pSide) {
        String path = pBlock.getId().getPath();

        Function<PowerState, BlockModelBuilder> modelFunction = (state) -> {
            ResourceLocation location = null;
            switch (state) {
                case DISABLED, OFF -> location = modLoc(String.format("%s_off", pFace.getPath()));
                case STANDBY, ON -> location = modLoc(String.format("%s_on", pFace.getPath()));
            }
            return models().withExistingParent(String.format("%s_%s", path, state.getSerializedName()), mcLoc("block/cube"))
                    .texture("particle", pSide)
                    .texture("down", pSide)
                    .texture("up", pSide)
                    .texture("north", location)
                    .texture("south", pSide)
                    .texture("east", pSide)
                    .texture("west", pSide);
        };

        getVariantBuilder(pBlock.get()).forAllStates(blockState -> {
            Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFunction.apply(blockState.getValue(PowerStateProperty.POWER_STATE)))
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0)
                    .rotationY(direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
        itemModels().withExistingParent(path, modLoc(String.format("block/%s_off", path)));
    }
}
