package com.smashingmods.alchemistry;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

public class Config {

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {

        private static final String categoryAtomizer = "atomizer";
        private static final String categoryCompactor = "compactor";
        private static final String categoryCombiner = "combiner";
        private static final String categoryDissolver = "dissolver";
        private static final String categoryLiquifier = "liquifier";
        private static final String categoryFission = "fission";
        private static final String categoryFusion = "fusion";

        public static ModConfigSpec.IntValue atomizerEnergyCapacity;
        public static ModConfigSpec.IntValue atomizerEnergyPerTick;
        public static ModConfigSpec.IntValue atomizerTicksPerOperation;
        public static ModConfigSpec.IntValue atomizerFluidCapacity;

        public static ModConfigSpec.IntValue compactorEnergyCapacity;
        public static ModConfigSpec.IntValue compactorEnergyPerTick;
        public static ModConfigSpec.IntValue compactorTicksPerOperation;

        public static ModConfigSpec.IntValue combinerEnergyCapacity;
        public static ModConfigSpec.IntValue combinerEnergyPerTick;
        public static ModConfigSpec.IntValue combinerTicksPerOperation;

        public static ModConfigSpec.IntValue dissolverEnergyCapacity;
        public static ModConfigSpec.IntValue dissolverEnergyPerTick;
        public static ModConfigSpec.IntValue dissolverTicksPerOperation;

        public static ModConfigSpec.IntValue liquifierEnergyCapacity;
        public static ModConfigSpec.IntValue liquifierEnergyPerTick;
        public static ModConfigSpec.IntValue liquifierTicksPerOperation;
        public static ModConfigSpec.IntValue liquifierFluidCapacity;

        public static ModConfigSpec.IntValue fissionEnergyCapacity;
        public static ModConfigSpec.IntValue fissionEnergyPerTick;
        public static ModConfigSpec.IntValue fissionTicksPerOperation;

        public static ModConfigSpec.IntValue fusionEnergyCapacity;
        public static ModConfigSpec.IntValue fusionEnergyPerTick;
        public static ModConfigSpec.IntValue fusionTicksPerOperation;

        public Common(ModConfigSpec.Builder builder) {

            builder.comment("Chemical Atomizer").push(categoryAtomizer);
            atomizerEnergyCapacity = builder
                    .comment("Maximum energy capacity for the Atomizer.")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            atomizerEnergyPerTick = builder
                    .comment("Energy consumed per tick when Atomizer is processing.")
                    .comment("Default: 50 FE")
                    .defineInRange("energyPerTick", 50, 0, Integer.MAX_VALUE);
            atomizerTicksPerOperation = builder
                    .comment("Ticks per operation when using the Atomizer.")
                    .comment("Default: 50 ticks")
                    .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
            atomizerFluidCapacity = builder
                    .comment("Fluid capacity in Atomizer tank.")
                    .comment("Default: 16000 (16 buckets)")
                    .defineInRange("fluidCapacity", 16000, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Chemical Compactor").push(categoryCompactor);
            compactorEnergyCapacity = builder
                    .comment("Maximum energy capacity for the Compactor.")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            compactorEnergyPerTick = builder
                    .comment("Energy consumed per tick when Compactor is processing.")
                    .comment("Default: 50 FE")
                    .defineInRange("energyPerTick", 50, 0, Integer.MAX_VALUE);
            compactorTicksPerOperation = builder
                    .comment("Ticks per operation when using the Compactor.")
                    .comment("Default: 50 ticks")
                    .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Chemical Combiner").push(categoryCombiner);
            combinerEnergyCapacity = builder
                    .comment("Maximum energy capacity for the Combiner.")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            combinerEnergyPerTick = builder
                    .comment("Energy consumed per tick when Combiner is processing.")
                    .comment("Default: 200 FE")
                    .defineInRange("energyPerTick", 200, 0, Integer.MAX_VALUE);
            combinerTicksPerOperation = builder
                    .comment("Ticks per operation when using the Combiner.")
                    .comment("Default: 50 ticks")
                    .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Chemical Dissolver").push(categoryDissolver);
            dissolverEnergyCapacity = builder
                    .comment("Maximum energy capacity for the Dissolver.")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            dissolverEnergyPerTick = builder
                    .comment("Energy consumed per tick when Dissolver is processing.")
                    .comment("Default: 100 FE")
                    .defineInRange("energyPerTick", 100, 0, Integer.MAX_VALUE);
            dissolverTicksPerOperation = builder
                    .comment("Ticks per operation when using the Dissolver.")
                    .comment("Default: 50 ticks")
                    .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Chemical Liquifier").push(categoryLiquifier);
            liquifierEnergyCapacity = builder
                    .comment("Maximum energy capacity for the Liquifier.")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            liquifierEnergyPerTick = builder
                    .comment("Energy consumed per tick when Liquifier is processing")
                    .comment("Default: 50 FE")
                    .defineInRange("energyPerTick", 50, 0, Integer.MAX_VALUE);
            liquifierTicksPerOperation = builder
                    .comment("Ticks per operation when using the Liquifier.")
                    .comment("Default: 100 ticks")
                    .defineInRange("ticksPerOperation", 100, 1, Integer.MAX_VALUE);
            liquifierFluidCapacity = builder
                    .comment("Fluid capacity in Liquifier tank.")
                    .comment("Default: 16000 (16 buckets)")
                    .defineInRange("fluidCapacity", 16000, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Fission").push(categoryFission);
            fissionEnergyCapacity = builder
                    .comment("Maximum energy capacity of the Fission multiblock.")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            fissionEnergyPerTick = builder
                    .comment("Energy consumed per tick when the Fission multiblock is processing.")
                    .comment("Default: 300 FE")
                    .defineInRange("energyPerTick", 300, 0, Integer.MAX_VALUE);
            fissionTicksPerOperation = builder
                    .comment("Ticks per operation when using the Fission multiblock")
                    .comment("Default: 50 ticks")
                    .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Fusion").push(categoryFusion);
            fusionEnergyCapacity = builder
                    .comment("Maximum energy capacity of the Fusion multiblock")
                    .comment("Default: 100000 (100k FE)")
                    .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
            fusionEnergyPerTick = builder
                    .comment("Energy consumed per tick when the Fusion multiblock is processing")
                    .comment("Default: 300 FE")
                    .defineInRange("energyPerTick", 300, 0, Integer.MAX_VALUE);
            fusionTicksPerOperation = builder
                    .comment("Ticks per operation when using the Fusion multiblock")
                    .comment("Default: 50 ticks")
                    .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static void loadConfig(ModConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.correct(configData);
    }
}
