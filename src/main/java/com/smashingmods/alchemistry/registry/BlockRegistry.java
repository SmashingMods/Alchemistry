package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlock;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlock;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlock;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlock;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlock;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlock;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlock;
import com.smashingmods.alchemistry.common.block.reactor.*;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<Block> ATOMIZER = BLOCKS.<Block>registerBlock("atomizer", AtomizerBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> COMPACTOR = BLOCKS.<Block>registerBlock("compactor", CompactorBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> COMBINER = BLOCKS.<Block>registerBlock("combiner", CombinerBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> DISSOLVER = BLOCKS.<Block>registerBlock("dissolver", DissolverBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> LIQUIFIER = BLOCKS.<Block>registerBlock("liquifier", LiquifierBlock::new, AbstractProcessingBlock.machineProperties());

    public static final DeferredBlock<Block> FISSION_CONTROLLER = BLOCKS.<Block>registerBlock("fission_chamber_controller", FissionControllerBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> FUSION_CONTROLLER = BLOCKS.<Block>registerBlock("fusion_chamber_controller", FusionControllerBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<RotatedPillarBlock> FISSION_CORE = BLOCKS.<RotatedPillarBlock>registerBlock("fission_core", ReactorCoreBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f));
    public static final DeferredBlock<RotatedPillarBlock> FUSION_CORE = BLOCKS.<RotatedPillarBlock>registerBlock("fusion_core", ReactorCoreBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f));
    public static final DeferredBlock<Block> REACTOR_CASING = BLOCKS.<Block>registerBlock("reactor_casing", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f));
    public static final DeferredBlock<Block> REACTOR_GLASS = BLOCKS.<Block>registerBlock("reactor_glass", ReactorGlassBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(2.0f));
    public static final DeferredBlock<Block> REACTOR_ENERGY = BLOCKS.<Block>registerBlock("reactor_energy", ReactorEnergyBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> REACTOR_INPUT = BLOCKS.<Block>registerBlock("reactor_input", ReactorInputBlock::new, AbstractProcessingBlock.machineProperties());
    public static final DeferredBlock<Block> REACTOR_OUTPUT = BLOCKS.<Block>registerBlock("reactor_output", ReactorOutputBlock::new, AbstractProcessingBlock.machineProperties());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
