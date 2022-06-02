package com.smashingmods.alchemistry;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

public class Config {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {

        private static final String categoryAtomizer = "atomizer";
        private static final String categoryCompactor = "compactor";
        private static final String categoryCombiner = "combiner";
        private static final String categoryDissolver = "dissolver";
        private static final String categoryEvaporator = "evaporator";
        private static final String categoryLiquifier = "liquifier";
        private static final String categoryFission = "fission";
        private static final String categoryFusion = "fusion";

        public static IntValue atomizerEnergyCapacity;
        public static IntValue atomizerEnergyPerTick;
        public static IntValue atomizerTicksPerOperation;
        public static IntValue atomizerFluidCapacity;

        public static IntValue compactorEnergyCapacity;
        public static IntValue compactorEnergyPerTick;
        public static IntValue compactorTicksPerOperation;

        public static IntValue combinerEnergyCapacity;
        public static IntValue combinerEnergyPerTick;
        public static IntValue combinerTicksPerOperation;

        public static IntValue dissolverEnergyCapacity;
        public static IntValue dissolverEnergyPerTick;
        public static IntValue dissolverTicksPerOperation;

        public static IntValue evaporatorTicksPerOperation;

        public static IntValue liquifierEnergyCapacity;
        public static IntValue liquifierEnergyPerTick;
        public static IntValue liquifierTicksPerOperation;
        public static IntValue liquifierFluidCapacity;

        public static IntValue fissionEnergyCapacity;
        public static IntValue fissionEnergyPerTick;
        public static IntValue fissionTicksPerOperation;

        public static IntValue fusionEnergyCapacity;
        public static IntValue fusionEnergyPerTick;
        public static IntValue fusionTicksPerOperation;

        public Common(ForgeConfigSpec.Builder builder) {

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

            builder.comment("Chemical Evaporator").push(categoryEvaporator);
            evaporatorTicksPerOperation = builder
                    .comment("Ticks per operation when using the Evaporator.")
                    .comment("Default: 160")
                    .defineInRange("ticksPerOperation", 160, 1, Integer.MAX_VALUE);
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

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
