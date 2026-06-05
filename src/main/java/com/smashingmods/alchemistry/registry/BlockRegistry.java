package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlock;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlock;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlock;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlock;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlock;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlock;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlock;
import com.smashingmods.alchemistry.common.block.reactor.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<Block> ATOMIZER = BLOCKS.register("atomizer", AtomizerBlock::new);
    public static final DeferredBlock<Block> COMPACTOR = BLOCKS.register("compactor", CompactorBlock::new);
    public static final DeferredBlock<Block> COMBINER = BLOCKS.register("combiner", CombinerBlock::new);
    public static final DeferredBlock<Block> DISSOLVER = BLOCKS.register("dissolver", DissolverBlock::new);
    public static final DeferredBlock<Block> LIQUIFIER = BLOCKS.register("liquifier", LiquifierBlock::new);

    public static final DeferredBlock<Block> FISSION_CONTROLLER = BLOCKS.register("fission_chamber_controller", FissionControllerBlock::new);
    public static final DeferredBlock<Block> FUSION_CONTROLLER = BLOCKS.register("fusion_chamber_controller", FusionControllerBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> FISSION_CORE = BLOCKS.register("fission_core", ReactorCoreBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> FUSION_CORE = BLOCKS.register("fusion_core", ReactorCoreBlock::new);
    public static final DeferredBlock<Block> REACTOR_CASING = BLOCKS.register("reactor_casing", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f)));
    public static final DeferredBlock<Block> REACTOR_GLASS = BLOCKS.register("reactor_glass", ReactorGlassBlock::new);
    public static final DeferredBlock<Block> REACTOR_ENERGY = BLOCKS.register("reactor_energy", ReactorEnergyBlock::new);
    public static final DeferredBlock<Block> REACTOR_INPUT = BLOCKS.register("reactor_input", ReactorInputBlock::new);
    public static final DeferredBlock<Block> REACTOR_OUTPUT = BLOCKS.register("reactor_output", ReactorOutputBlock::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
