package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemylib.api.blockentity.power.PowerState;
import com.smashingmods.alchemylib.api.blockentity.power.PowerStateProperty;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Generates the mod's blockstates, block models and block-item model definitions. 1.21.4 replaced the
 * NeoForge {@code client.model.generators.BlockStateProvider}/{@code ItemModelProvider} with the vanilla
 * {@link ModelProvider}: blockstates are assembled from {@link MultiVariantGenerator}/{@link MultiVariant}/
 * {@link PropertyDispatch}, block models from {@link ModelTemplate} and the surviving NeoForge
 * {@link ExtendedModelTemplateBuilder} (for the per-face cube models that carry a render type), and the
 * block-item layer now lives in the {@code items/<id>.json} definitions emitted through {@link ItemModelOutput}.
 *
 * <p>1.21.5 reworked the blockstate builders again: {@code Variant}/{@code VariantProperties} were replaced by
 * {@link MultiVariant} (a model reference produced by {@link BlockModelGenerators#plainVariant}) plus
 * {@link VariantMutator} rotations ({@link BlockModelGenerators#Y_ROT_90} and friends), and the
 * {@code BlockStateGenerator} output type is now {@link BlockModelDefinitionGenerator}.
 * {@link MultiVariantGenerator#dispatch} replaces {@code multiVariant}; a single-model block layers its facing
 * rotation through {@link MultiVariantGenerator#with(PropertyDispatch)} of a {@code PropertyDispatch<VariantMutator>},
 * and a block with a distinct model per state dispatches a {@code PropertyDispatch<MultiVariant>} instead.
 *
 * <p>Every block this provider owns is covered here, so {@code ModelProvider}'s completeness check doubles
 * as a guarantee that no block or block-item is missed. The compactor is excluded from
 * {@link #getKnownBlocks()}/{@link #getKnownItems()}: its blockstate, custom-geometry model and item model
 * are authored by hand under {@code src/main/resources}, and unlike the old NeoForge {@code BlockStateProvider}
 * the vanilla {@link ModelProvider} validates coverage, so it would otherwise flag the compactor as missing.
 *
 * <p>Output is preserved from 1.21.3: the machines reference their authored {@code block/<name>} models, the
 * chamber controllers and reactor I/O faces keep their per-power-state cube models, the reactor cores keep the
 * {@code cube_column}/{@code cube_column_horizontal} pair and the reactor casing/glass keep their render-typed
 * {@code cube_all} models.
 */
public class BlockStateGenerator extends ModelProvider {

    public BlockStateGenerator(PackOutput pOutput) {
        super(pOutput, Alchemistry.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators pBlockModels, ItemModelGenerators pItemModels) {
        Consumer<BlockModelDefinitionGenerator> blockStateOutput = pBlockModels.blockStateOutput;
        BiConsumer<ResourceLocation, ModelInstance> modelOutput = pBlockModels.modelOutput;
        ItemModelOutput itemOutput = pBlockModels.itemModelOutput;

        registerMachineModel(BlockRegistry.ATOMIZER, blockStateOutput, itemOutput);
        registerMachineModel(BlockRegistry.COMBINER, blockStateOutput, itemOutput);
        registerMachineModel(BlockRegistry.DISSOLVER, blockStateOutput, itemOutput);
        registerMachineModel(BlockRegistry.LIQUIFIER, blockStateOutput, itemOutput);

        registerControllerModel(BlockRegistry.FISSION_CONTROLLER, blockStateOutput, modelOutput, itemOutput);
        registerControllerModel(BlockRegistry.FUSION_CONTROLLER, blockStateOutput, modelOutput, itemOutput);
        registerAxisBlock(BlockRegistry.FISSION_CORE, blockStateOutput, modelOutput, itemOutput);
        registerAxisBlock(BlockRegistry.FUSION_CORE, blockStateOutput, modelOutput, itemOutput);
        registerSimpleBlockWithRenderType(BlockRegistry.REACTOR_CASING, "solid", blockStateOutput, modelOutput, itemOutput);
        registerSimpleBlockWithRenderType(BlockRegistry.REACTOR_GLASS, "translucent", blockStateOutput, modelOutput, itemOutput);
        registerReactorIOModels(blockStateOutput, modelOutput, itemOutput);
    }

    /**
     * The horizontal-facing rotation the machines, chamber controllers and reactor I/O faces share, as a
     * {@code PropertyDispatch<VariantMutator>} layered onto a base model. Mirrors the (private) vanilla
     * {@code BlockModelGenerators#ROTATION_HORIZONTAL_FACING}: the model faces north by default, so north is
     * unrotated ({@link BlockModelGenerators#NOP}) and east/south/west rotate +90/180/270 about the Y axis.
     */
    private static PropertyDispatch<VariantMutator> horizontalFacingDispatch() {
        return PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                .select(Direction.NORTH, BlockModelGenerators.NOP)
                .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                .select(Direction.WEST, BlockModelGenerators.Y_ROT_270);
    }

    /** The Y-axis rotation mutator that orients a north-facing model towards the given horizontal direction. */
    private static VariantMutator facingRotation(Direction pDirection) {
        return switch (pDirection) {
            case EAST -> BlockModelGenerators.Y_ROT_90;
            case SOUTH -> BlockModelGenerators.Y_ROT_180;
            case WEST -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP; // north is unrotated
        };
    }

    /**
     * A {@code minecraft:block/cube} model with the six faces, particle and render type set explicitly, built
     * through the NeoForge {@link ExtendedModelTemplateBuilder} (the vanilla {@link ModelTemplate} carries no
     * render type or per-face textures).
     */
    private static void generateCubeModel(ResourceLocation pModel, String pRenderType, TextureMapping pTextures, BiConsumer<ResourceLocation, ModelInstance> pModelOutput) {
        ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.withDefaultNamespace("block/cube"))
                .renderType(pRenderType)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.DOWN)
                .requiredTextureSlot(TextureSlot.UP)
                .requiredTextureSlot(TextureSlot.NORTH)
                .requiredTextureSlot(TextureSlot.SOUTH)
                .requiredTextureSlot(TextureSlot.EAST)
                .requiredTextureSlot(TextureSlot.WEST)
                .build()
                .create(pModel, pTextures, pModelOutput);
    }

    /** The plain {@code items/<id>.json} pointing at a block model, replacing the old {@code models/item/<id>.json} parent indirection. */
    private void registerBlockItemModel(DeferredBlock<? extends Block> pBlock, ResourceLocation pModel, ItemModelOutput pItemOutput) {
        pItemOutput.accept(pBlock.get().asItem(), ItemModelUtils.plainModel(pModel));
    }

    private void registerMachineModel(DeferredBlock<Block> pBlock, Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, ItemModelOutput pItemOutput) {
        ResourceLocation model = modLocation(String.format("block/%s", pBlock.getId().getPath()));
        pBlockStateOutput.accept(MultiVariantGenerator.dispatch(pBlock.get(), BlockModelGenerators.plainVariant(model)).with(horizontalFacingDispatch()));
        registerBlockItemModel(pBlock, model, pItemOutput);
    }

    private void registerControllerModel(DeferredBlock<Block> pBlock, Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput, ItemModelOutput pItemOutput) {
        String path = pBlock.getId().getPath();
        String type = path.split("_")[0];
        ResourceLocation back = modLocation(String.format("block/%s_chamber_controller_back", type));
        ResourceLocation hSide = modLocation(String.format("block/%s_chamber_controller_hside", type));
        ResourceLocation vSide = modLocation(String.format("block/%s_chamber_controller_vside", type));

        PropertyDispatch.C2<MultiVariant, Direction, PowerState> dispatch = PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, PowerStateProperty.POWER_STATE);
        for (PowerState state : PowerState.values()) {
            // The per-state model and its north (front) face share the same name, e.g. fission_chamber_controller_off.
            ResourceLocation model = modLocation(String.format("block/%s_%s", path, state.getSerializedName()));
            generateCubeModel(model, "solid", new TextureMapping()
                    .put(TextureSlot.PARTICLE, hSide)
                    .put(TextureSlot.DOWN, vSide)
                    .put(TextureSlot.UP, vSide)
                    .put(TextureSlot.NORTH, model)
                    .put(TextureSlot.SOUTH, back)
                    .put(TextureSlot.EAST, hSide)
                    .put(TextureSlot.WEST, hSide), pModelOutput);
            addFacingPowerStateVariants(dispatch, state, model);
        }
        pBlockStateOutput.accept(MultiVariantGenerator.dispatch(pBlock.get()).with(dispatch));
        registerBlockItemModel(pBlock, modLocation(String.format("block/%s_standby", path)), pItemOutput);
    }

    private void registerAxisBlock(DeferredBlock<RotatedPillarBlock> pBlock, Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput, ItemModelOutput pItemOutput) {
        String path = pBlock.getId().getPath();
        ResourceLocation end = modLocation(String.format("block/%s_end", path));
        ResourceLocation side = modLocation(String.format("block/%s_side", path));
        TextureMapping textures = new TextureMapping().put(TextureSlot.END, end).put(TextureSlot.SIDE, side);

        ResourceLocation vertical = new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/cube_column")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE)
                .create(modLocation(String.format("block/%s", path)), textures, pModelOutput);
        ResourceLocation horizontal = new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/cube_column_horizontal")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE)
                .create(modLocation(String.format("block/%s_horizontal", path)), textures, pModelOutput);

        pBlockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(pBlock.get(), BlockModelGenerators.plainVariant(vertical), BlockModelGenerators.plainVariant(horizontal)));
        registerBlockItemModel(pBlock, vertical, pItemOutput);
    }

    private void registerSimpleBlockWithRenderType(DeferredBlock<Block> pBlock, String pRenderType, Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput, ItemModelOutput pItemOutput) {
        String path = pBlock.getId().getPath();
        ResourceLocation model = modLocation(String.format("block/%s", path));
        ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.withDefaultNamespace("block/cube_all"))
                .renderType(pRenderType)
                .requiredTextureSlot(TextureSlot.ALL)
                .build()
                .create(model, new TextureMapping().put(TextureSlot.ALL, modLocation(String.format("block/%s", path))), pModelOutput);
        pBlockStateOutput.accept(MultiVariantGenerator.dispatch(pBlock.get(), BlockModelGenerators.plainVariant(model)));
        registerBlockItemModel(pBlock, model, pItemOutput);
    }

    private void registerReactorIOModels(Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput, ItemModelOutput pItemOutput) {
        ResourceLocation side = modLocation("block/reactor_casing");
        ResourceLocation input = modLocation("block/reactor_input");
        ResourceLocation output = modLocation("block/reactor_output");
        ResourceLocation energy = modLocation("block/reactor_energy");

        registerFaceBlock(BlockRegistry.REACTOR_INPUT, input, side, pBlockStateOutput, pModelOutput, pItemOutput);
        registerFaceBlock(BlockRegistry.REACTOR_OUTPUT, output, side, pBlockStateOutput, pModelOutput, pItemOutput);
        registerPoweredFaceBlock(BlockRegistry.REACTOR_ENERGY, energy, side, pBlockStateOutput, pModelOutput, pItemOutput);
    }

    private void registerFaceBlock(DeferredBlock<Block> pBlock, ResourceLocation pFace, ResourceLocation pSide, Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput, ItemModelOutput pItemOutput) {
        String path = pBlock.getId().getPath();
        ResourceLocation model = modLocation(String.format("block/%s", path));
        generateCubeModel(model, "solid", faceTextures(pFace, pSide), pModelOutput);
        pBlockStateOutput.accept(MultiVariantGenerator.dispatch(pBlock.get(), BlockModelGenerators.plainVariant(model)).with(horizontalFacingDispatch()));
        registerBlockItemModel(pBlock, model, pItemOutput);
    }

    @SuppressWarnings("SameParameterValue")
    private void registerPoweredFaceBlock(DeferredBlock<Block> pBlock, ResourceLocation pFace, ResourceLocation pSide, Consumer<BlockModelDefinitionGenerator> pBlockStateOutput, BiConsumer<ResourceLocation, ModelInstance> pModelOutput, ItemModelOutput pItemOutput) {
        String path = pBlock.getId().getPath();

        PropertyDispatch.C2<MultiVariant, Direction, PowerState> dispatch = PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, PowerStateProperty.POWER_STATE);
        for (PowerState state : PowerState.values()) {
            ResourceLocation face = switch (state) {
                case DISABLED, OFF -> modLocation(String.format("%s_off", pFace.getPath()));
                case STANDBY, ON -> modLocation(String.format("%s_on", pFace.getPath()));
            };
            ResourceLocation model = modLocation(String.format("block/%s_%s", path, state.getSerializedName()));
            generateCubeModel(model, "solid", faceTextures(face, pSide), pModelOutput);
            addFacingPowerStateVariants(dispatch, state, model);
        }
        pBlockStateOutput.accept(MultiVariantGenerator.dispatch(pBlock.get()).with(dispatch));
        registerBlockItemModel(pBlock, modLocation(String.format("block/%s_off", path)), pItemOutput);
    }

    /** The cube texture mapping shared by the reactor I/O faces: the given face on north, the casing side everywhere else. */
    private static TextureMapping faceTextures(ResourceLocation pFace, ResourceLocation pSide) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, pSide)
                .put(TextureSlot.DOWN, pSide)
                .put(TextureSlot.UP, pSide)
                .put(TextureSlot.NORTH, pFace)
                .put(TextureSlot.SOUTH, pSide)
                .put(TextureSlot.EAST, pSide)
                .put(TextureSlot.WEST, pSide);
    }

    /** Selects the given model for all four horizontal facings at the given power state, layering on the shared facing rotation. */
    private static void addFacingPowerStateVariants(PropertyDispatch.C2<MultiVariant, Direction, PowerState> pDispatch, PowerState pState, ResourceLocation pModel) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            pDispatch.select(direction, pState, BlockModelGenerators.plainVariant(pModel).with(facingRotation(direction)));
        }
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.listElements()
                .filter(holder -> holder.getKey().location().getNamespace().equals(modId))
                .filter(holder -> !holder.is(BlockRegistry.COMPACTOR.getId()));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> holder.getKey().location().getNamespace().equals(modId))
                .filter(holder -> !holder.is(BlockRegistry.COMPACTOR.getId()));
    }
}
