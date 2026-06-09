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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

@SuppressWarnings("ConstantConditions")
public class BlockEntityRegistry {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtomizerBlockEntity>> ATOMIZER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("atomizer_block_entity",
            () -> new BlockEntityType<>(AtomizerBlockEntity::new, BlockRegistry.ATOMIZER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompactorBlockEntity>> COMPACTOR_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("compactor_block_entity",
            () -> new BlockEntityType<>(CompactorBlockEntity::new, BlockRegistry.COMPACTOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombinerBlockEntity>> COMBINER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("combiner_block_entity",
            () -> new BlockEntityType<>(CombinerBlockEntity::new, BlockRegistry.COMBINER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DissolverBlockEntity>> DISSOLVER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("dissolver_block_entity",
            () -> new BlockEntityType<>(DissolverBlockEntity::new, BlockRegistry.DISSOLVER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquifierBlockEntity>> LIQUIFIER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("liquifier_block_entity",
            () -> new BlockEntityType<>(LiquifierBlockEntity::new, BlockRegistry.LIQUIFIER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FissionControllerBlockEntity>> FISSION_CONTROLLER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("fission_controller_block_entity",
            () -> new BlockEntityType<>(FissionControllerBlockEntity::new, BlockRegistry.FISSION_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FusionControllerBlockEntity>> FUSION_CONTROLLER_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("fusion_controller_block_entity",
            () -> new BlockEntityType<>(FusionControllerBlockEntity::new, BlockRegistry.FUSION_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorEnergyBlockEntity>> REACTOR_ENERGY_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("reactor_energy_block_entity",
            () -> new BlockEntityType<>(ReactorEnergyBlockEntity::new, BlockRegistry.REACTOR_ENERGY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorInputBlockEntity>> REACTOR_INPUT_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("reactor_input_block_entity",
            () -> new BlockEntityType<>(ReactorInputBlockEntity::new, BlockRegistry.REACTOR_INPUT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorOutputBlockEntity>> REACTOR_OUTPUT_BLOCK_ENTITY
            = BLOCK_ENTITY_TYPES.register("reactor_output_block_entity",
            () -> new BlockEntityType<>(ReactorOutputBlockEntity::new, BlockRegistry.REACTOR_OUTPUT.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
