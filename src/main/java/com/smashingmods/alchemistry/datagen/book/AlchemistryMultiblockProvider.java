package com.smashingmods.alchemistry.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

/**
 * Defines the dense multiblock patterns referenced by the guidebook's reactor multiblock pages
 * ({@code alchemistry:fission_reactor} and {@code alchemistry:fusion_reactor}).
 *
 * <p>Both reactors share one 5x5x5 layout; only the controller and core blocks differ (fission vs. fusion).
 * Modonomicon's dense format orders the pattern layers top-to-bottom (first layer = top, last = bottom), the
 * same convention the structures were authored in, so the layers are listed top-down: a solid casing cap, three
 * hollow body layers around the cores (with the chamber controller on the lowest body layer), and the bottom
 * floor carrying the energy/input/output blocks and the {@code 0} anchor. Spaces are the inert air pocket around
 * the cores; the builder maps them to "any" automatically, so they need no explicit mapping.
 */
public class AlchemistryMultiblockProvider extends MultiblockProvider {

    public AlchemistryMultiblockProvider(PackOutput packOutput) {
        super(packOutput, MODID);
    }

    @Override
    public void buildMultiblocks() {
        add(modLoc("fission_reactor"), reactor(BlockRegistry.FISSION_CONTROLLER::get, BlockRegistry.FISSION_CORE::get));
        add(modLoc("fusion_reactor"), reactor(BlockRegistry.FUSION_CONTROLLER::get, BlockRegistry.FUSION_CORE::get));
    }

    private DenseMultiblockBuilder reactor(Supplier<? extends Block> controller, Supplier<? extends Block> core) {
        return new DenseMultiblockBuilder()
                .layer("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                .layer("CGGGC", "G   G", "G F G", "G   G", "CGGGC")
                .layer("CGGGC", "G   G", "G F G", "G   G", "CGGGC")
                .layer("CGGGC", "G   G", "G F X", "G   G", "CGGGC")
                .layer("CCCCC", "CCCCI", "CC0CE", "CCCCO", "CCCCC")
                .block('C', BlockRegistry.REACTOR_CASING::get)
                .block('G', BlockRegistry.REACTOR_GLASS::get)
                .block('F', core)
                .block('0', BlockRegistry.REACTOR_CASING::get)
                .blockstate('X', controller, "[facing=east]")
                .blockstate('E', BlockRegistry.REACTOR_ENERGY::get, "[facing=east]")
                .blockstate('I', BlockRegistry.REACTOR_INPUT::get, "[facing=east]")
                .blockstate('O', BlockRegistry.REACTOR_OUTPUT::get, "[facing=east]");
    }
}
