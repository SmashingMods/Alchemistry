package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlock;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlock;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlock;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlock;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlock;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlock;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> ATOMIZER = BLOCKS.register("atomizer", AtomizerBlock::new);
    public static final RegistryObject<Block> COMPACTOR = BLOCKS.register("compactor", CompactorBlock::new);
    public static final RegistryObject<Block> COMBINER = BLOCKS.register("combiner", CombinerBlock::new);
    public static final RegistryObject<Block> DISSOLVER = BLOCKS.register("dissolver", DissolverBlock::new);
    public static final RegistryObject<Block> LIQUIFIER = BLOCKS.register("liquifier", LiquifierBlock::new);
    public static final RegistryObject<Block> FISSION_CONTROLLER = BLOCKS.register("fission_controller", FissionControllerBlock::new);
    public static final RegistryObject<Block> FUSION_CONTROLLER = BLOCKS.register("fusion_controller", FusionControllerBlock::new);
    public static final RegistryObject<Block> REACTOR_CASING = BLOCKS.register("reactor_casing", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)));
    public static final RegistryObject<RotatedPillarBlock> FISSION_CORE = BLOCKS.register("fission_core", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)));
    public static final RegistryObject<RotatedPillarBlock> FUSION_CORE = BLOCKS.register("fusion_core", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2.0f)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
