package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class BlockEntityRegistry {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

    public static final RegistryObject<BlockEntityType<AtomizerBlockEntity>> ATOMIZER_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("atomizer_block_entity",
            () -> BlockEntityType.Builder.of(AtomizerBlockEntity::new, BlockRegistry.ATOMIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CompactorBlockEntity>> COMPACTOR_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("compactor_block_entity",
            () -> BlockEntityType.Builder.of(CompactorBlockEntity::new, BlockRegistry.COMPACTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CombinerBlockEntity>> COMBINER_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("combiner_block_entity",
            () -> BlockEntityType.Builder.of(CombinerBlockEntity::new, BlockRegistry.COMBINER.get()).build(null));

    public static final RegistryObject<BlockEntityType<DissolverBlockEntity>> DISSOLVER_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("dissolver_block_entity",
            () -> BlockEntityType.Builder.of(DissolverBlockEntity::new, BlockRegistry.DISSOLVER.get()).build(null));

    public static final RegistryObject<BlockEntityType<LiquifierBlockEntity>> LIQUIFIER_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("liquifier_block_entity",
            () -> BlockEntityType.Builder.of(LiquifierBlockEntity::new, BlockRegistry.LIQUIFIER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FissionControllerBlockEntity>> FISSION_CONTROLLER_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("fission_controller_block_entity",
            () -> BlockEntityType.Builder.of(FissionControllerBlockEntity::new, BlockRegistry.FISSION_CONTROLLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FusionControllerBlockEntity>> FUSION_CONTROLLER_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("fusion_controller_block_entity",
            () -> BlockEntityType.Builder.of(FusionControllerBlockEntity::new, BlockRegistry.FUSION_CONTROLLER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
