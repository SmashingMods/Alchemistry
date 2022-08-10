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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
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
        registerSimpleBlockWithRenderType(BlockRegistry.REACTOR_CASING, "solid");
        registerSimpleBlockWithRenderType(BlockRegistry.REACTOR_GLASS, "translucent");
        registerReactorIOModels();
    }

    private void registerSimpleBlock(RegistryObject<Block> pBlock) {
        simpleBlock(pBlock.get());
        registerBlockItemModel(pBlock);
    }

    private void registerSimpleBlockWithRenderType(RegistryObject<Block> pBlock, String pRenderType) {
//        ConfiguredModel.builder().modelFile(models().cubeAll("", blockTexture(pBlock.get())).renderType(pRenderType)).build();

        ConfiguredModel[] model = ConfiguredModel.builder().modelFile(models().withExistingParent(pBlock.getId().getPath(), mcLoc("block/cube_all"))
                        .renderType(pRenderType)
                        .texture("all", blockTexture(pBlock.get())))
                .build();
        getVariantBuilder(pBlock.get()).partialState().setModels(model);
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

        getVariantBuilder(controller).forAllStates(blockState -> {
            Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            String path = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(controller)).getPath();
            String type = path.split("_")[0];
            String modelName = String.format("block/%s_%s", path, blockState.getValue(PowerStateProperty.POWER_STATE).getSerializedName());
            ResourceLocation face = modLoc(modelName);
            ResourceLocation back = modLoc(String.format("block/%s_chamber_controller_back", type));
            ResourceLocation hSide = modLoc(String.format("block/%s_chamber_controller_hside", type));
            ResourceLocation vSide = modLoc(String.format("block/%s_chamber_controller_vside", type));
            return ConfiguredModel.builder()
                    .modelFile(models().withExistingParent(modelName, mcLoc("block/cube"))
                            .renderType("solid")
                            .texture("particle", hSide)
                            .texture("down", vSide)
                            .texture("up", vSide)
                            .texture("north", face)
                            .texture("south", back)
                            .texture("east", hSide)
                            .texture("west", hSide))
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0)
                    .rotationY(direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
        itemModels().withExistingParent(pBlock.getId().getPath(), modLoc(String.format("block/%s_standby", pBlock.getId().getPath())));
    }

    private void registerReactorIOModels() {
        ResourceLocation side = modLoc("block/reactor_casing");
        ResourceLocation input = modLoc("block/reactor_input");
        ResourceLocation output = modLoc("block/reactor_output");
        ResourceLocation energy = modLoc("block/reactor_energy");

        registerFaceBlock(BlockRegistry.REACTOR_INPUT, input, side);
        registerFaceBlock(BlockRegistry.REACTOR_OUTPUT, output, side);
        registerPoweredFaceBlock(BlockRegistry.REACTOR_ENERGY, energy, side);
    }

    private void registerFaceBlock(RegistryObject<Block> pBlock, ResourceLocation pFace, ResourceLocation pSide) {
        String path = pBlock.getId().getPath();
        getVariantBuilder(pBlock.get()).forAllStates(blockState -> {
            Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
            ModelFile modelFile = models().withExistingParent(path, mcLoc("block/cube"))
                    .renderType("solid")
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
                    .renderType("solid")
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
