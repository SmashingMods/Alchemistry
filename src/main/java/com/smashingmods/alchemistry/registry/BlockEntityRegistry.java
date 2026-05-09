package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorEnergyBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorInputBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorOutputBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

@SuppressWarnings("ConstantConditions")
public class BlockEntityRegistry {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtomizerBlockEntity>> ATOMIZER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("atomizer_block_entity",
            () -> BlockEntityType.Builder.of(AtomizerBlockEntity::new, BlockRegistry.ATOMIZER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompactorBlockEntity>> COMPACTOR_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("compactor_block_entity",
            () -> BlockEntityType.Builder.of(CompactorBlockEntity::new, BlockRegistry.COMPACTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombinerBlockEntity>> COMBINER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("combiner_block_entity",
            () -> BlockEntityType.Builder.of(CombinerBlockEntity::new, BlockRegistry.COMBINER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DissolverBlockEntity>> DISSOLVER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("dissolver_block_entity",
            () -> BlockEntityType.Builder.of(DissolverBlockEntity::new, BlockRegistry.DISSOLVER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquifierBlockEntity>> LIQUIFIER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("liquifier_block_entity",
            () -> BlockEntityType.Builder.of(LiquifierBlockEntity::new, BlockRegistry.LIQUIFIER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FissionControllerBlockEntity>> FISSION_CONTROLLER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("fission_controller_block_entity",
            () -> BlockEntityType.Builder.of(FissionControllerBlockEntity::new, BlockRegistry.FISSION_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FusionControllerBlockEntity>> FUSION_CONTROLLER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("fusion_controller_block_entity",
            () -> BlockEntityType.Builder.of(FusionControllerBlockEntity::new, BlockRegistry.FUSION_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorEnergyBlockEntity>> REACTOR_ENERGY_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("reactor_energy_block_entity",
            () -> BlockEntityType.Builder.of(ReactorEnergyBlockEntity::new, BlockRegistry.REACTOR_ENERGY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorInputBlockEntity>> REACTOR_INPUT_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("reactor_input_block_entity",
            () -> BlockEntityType.Builder.of(ReactorInputBlockEntity::new, BlockRegistry.REACTOR_INPUT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorOutputBlockEntity>> REACTOR_OUTPUT_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("reactor_output_block_entity",
            () -> BlockEntityType.Builder.of(ReactorOutputBlockEntity::new, BlockRegistry.REACTOR_OUTPUT.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    /**
     * Registers item / fluid / energy capabilities for each Alchemistry block entity. Called from a
     * {@link RegisterCapabilitiesEvent} listener registered in {@link com.smashingmods.alchemistry.Alchemistry}.
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATOMIZER_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COMPACTOR_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COMBINER_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DISSOLVER_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LIQUIFIER_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FISSION_CONTROLLER_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FUSION_CONTROLLER_BLOCK_ENTITY.get(), (be, side) -> be.getItemHandler(side));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ATOMIZER_BLOCK_ENTITY.get(), (be, side) -> be.getFluidHandler(side));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, LIQUIFIER_BLOCK_ENTITY.get(), (be, side) -> be.getFluidHandler(side));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATOMIZER_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, COMPACTOR_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, COMBINER_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DISSOLVER_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, LIQUIFIER_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FISSION_CONTROLLER_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FUSION_CONTROLLER_BLOCK_ENTITY.get(), (be, side) -> be.getEnergyStorage(side));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, REACTOR_ENERGY_BLOCK_ENTITY.get(),
                (be, side) -> be.getController() != null ? be.getController().getEnergyHandler() : null);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, REACTOR_INPUT_BLOCK_ENTITY.get(),
                (be, side) -> be.getController() != null ? be.getController().getInputHandler() : null);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, REACTOR_OUTPUT_BLOCK_ENTITY.get(),
                (be, side) -> be.getController() != null ? be.getController().getOutputHandler() : null);
    }
}
