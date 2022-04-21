package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockStateGenerator extends BlockStateProvider {

    private static ExistingFileHelper fileHelper;

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Alchemistry.MODID, exFileHelper);
        fileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile atomizerModel = new ModelFile.ExistingModelFile(modLoc("block/atomizer"), fileHelper);
        ModelFile combinerModel = new ModelFile.ExistingModelFile(modLoc("block/combiner"), fileHelper);
        ModelFile dissolverModel = new ModelFile.ExistingModelFile(modLoc("block/dissolver"), fileHelper);
        ModelFile evaporatorModel = new ModelFile.ExistingModelFile(modLoc("block/evaporator"), fileHelper);
        ModelFile liquifierModel = new ModelFile.ExistingModelFile(modLoc("block/liquifier"), fileHelper);

        registerBlockModel(BlockRegistry.ATOMIZER, atomizerModel);
        registerBlockModel(BlockRegistry.COMBINER, combinerModel);
        registerBlockModel(BlockRegistry.DISSOLVER, dissolverModel);
        registerBlockModel(BlockRegistry.EVAPORTOR, evaporatorModel);
        registerBlockModel(BlockRegistry.LIQUIFIER, liquifierModel);

//        cubeAll(BlockRegistry.FISSION_CONTROLLER.get());
//        cubeAll(BlockRegistry.FISSION_CASING_BLOCK.get());
//        cubeAll(BlockRegistry.FISSION_CORE_BLOCK.get());
//        cubeAll(BlockRegistry.FUSION_CONTROLLER.get());
//        cubeAll(BlockRegistry.FUSION_CASING_BLOCK.get());
//        cubeAll(BlockRegistry.FUSION_CORE_BLOCK.get());
    }

    private void registerBlockModel(RegistryObject<Block> pBlock, ModelFile pModelFile) {
        getVariantBuilder(pBlock.get()).forAllStates(blockState -> {
            Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(pModelFile)
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -19 : 0)
                    .rotationY(direction.getAxis() == Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) *90 : 0)
                    .build();
        });
    }
}
