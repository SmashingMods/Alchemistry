package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlock;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlock;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlock;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlock;
import com.smashingmods.alchemistry.common.block.evaporator.EvaporatorBlock;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlock;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlock;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlock;
import com.smashingmods.alchemistry.common.block.oldblocks.fission.FissionCasingBlock;
import com.smashingmods.alchemistry.common.block.oldblocks.fission.FissionCoreBlock;
import com.smashingmods.alchemistry.common.block.oldblocks.fusion.FusionCasingBlock;
import com.smashingmods.alchemistry.common.block.oldblocks.fusion.FusionCoreBlock;
import net.minecraft.world.level.block.Block;
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
    public static final RegistryObject<Block> EVAPORTOR = BLOCKS.register("evaporator", EvaporatorBlock::new);
    public static final RegistryObject<Block> LIQUIFIER = BLOCKS.register("liquifier", LiquifierBlock::new);
    public static final RegistryObject<Block> FISSION_CONTROLLER = BLOCKS.register("fission_controller", FissionControllerBlock::new);
    public static final RegistryObject<Block> FUSION_CONTROLLER = BLOCKS.register("fusion_controller", FusionControllerBlock::new);

    public static final RegistryObject<Block> FISSION_CASING_BLOCK = BLOCKS.register("fission_casing", FissionCasingBlock::new);
    public static final RegistryObject<Block> FISSION_CORE_BLOCK = BLOCKS.register("fission_core", FissionCoreBlock::new);
    public static final RegistryObject<Block> FUSION_CASING_BLOCK = BLOCKS.register("fusion_casing", FusionCasingBlock::new);
    public static final RegistryObject<Block> FUSION_CORE_BLOCK = BLOCKS.register("fusion_core", FusionCoreBlock::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
