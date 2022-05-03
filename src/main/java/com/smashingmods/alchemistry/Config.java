package com.smashingmods.alchemistry;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.nio.file.Path;

public class Config {
    public static String CATEGORY_FISSION = "fission";
    public static String CATEGORY_FUSION = "fusion";
    public static String CATEGORY_ATOMIZER = "atomizer";
    public static String CATEGORY_COMBINER = "combiner";
    public static String CATEGORY_DISSOLVER = "dissolver";
    public static String CATEGORY_EVAPORATOR = "evaporator";
    public static String CATEGORY_LIQUIFIER = "liquifier";


    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static IntValue ATOMIZER_ENERGY_CAPACITY;
    public static IntValue ATOMIZER_ENERGY_PER_TICK;
    public static IntValue ATOMIZER_TICKS_PER_OPERATION;

    public static IntValue COMBINER_ENERGY_CAPACITY;
    public static IntValue COMBINER_ENERGY_PER_TICK;
    public static IntValue COMBINER_TICKS_PER_OPERATION;

    public static IntValue DISSOLVER_ENERGY_CAPACITY;
    public static IntValue DISSOLVER_ENERGY_PER_TICK;
    public static IntValue DISSOLVER_TICKS_PER_OPERATION;

    public static IntValue EVAPORATOR_TICKS_PER_OPERATION;

    public static IntValue LIQUIFIER_ENERGY_CAPACITY;
    public static IntValue LIQUIFIER_ENERGY_PER_TICK;
    public static IntValue LIQUIFIER_TICKS_PER_OPERATION;

    public static IntValue FISSION_ENERGY_CAPACITY;
    public static IntValue FISSION_ENERGY_PER_TICK;
    public static IntValue FISSION_TICKS_PER_OPERATION;

    public static IntValue FUSION_ENERGY_CAPACITY;
    public static IntValue FUSION_ENERGY_PER_TICK;
    public static IntValue FUSION_TICKS_PER_OPERATION;

    static {
       // COMMON_BUILDER.comment("Machine Configs").push(CATEGORY_MACHINES);
        initMachineConfig();
        //COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    private static void initMachineConfig() {
        COMMON_BUILDER.comment("Atomizer").push(CATEGORY_ATOMIZER);

        ATOMIZER_ENERGY_CAPACITY = COMMON_BUILDER.comment("Maximum energy capacity of the Atomizer").comment("Default: 100000 (100k)")
                .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
        ATOMIZER_ENERGY_PER_TICK = COMMON_BUILDER.comment("Energy consumed per tick when Atomizer is processing").comment("Default: 50")
                .defineInRange("energyPerTick", 50, 0, Integer.MAX_VALUE);
        ATOMIZER_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Ticks per operation when using the Atomizer").comment("Default: 100")
                .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Chemical Combiner").push(CATEGORY_COMBINER);
        COMBINER_ENERGY_CAPACITY = COMMON_BUILDER.comment("Maximum energy capacity of the Combiner").comment("Default: 100000 (100k)")
                .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
        COMBINER_ENERGY_PER_TICK = COMMON_BUILDER.comment("Energy consumed per tick when Combiner is processing").comment("Default: 200")
                .defineInRange("energyPerTick", 200, 0, Integer.MAX_VALUE);
        COMBINER_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Ticks per operation when using the Combiner").comment("Default: 5")
                .defineInRange("ticksPerOperation", 50, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Chemical Dissolver").push(CATEGORY_DISSOLVER);
        DISSOLVER_ENERGY_CAPACITY = COMMON_BUILDER.comment("Maximum energy capacity of the Dissolver").comment("Default: 100000 (100k)")
                .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
        DISSOLVER_ENERGY_PER_TICK = COMMON_BUILDER.comment("Energy consumed per tick when Dissolver is processing").comment("Default: 200")
                .defineInRange("energyPerTick", 100, 0, Integer.MAX_VALUE);
        DISSOLVER_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Max output items per tick").comment("Default: 8")
                .defineInRange("maxOutputPerTick", 50, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Evaporator").push(CATEGORY_EVAPORATOR);
        EVAPORATOR_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Ticks per operation when using the Evaporator").comment("Default: 160")
                .defineInRange("ticksPerOperation", 160, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Liquifier").push(CATEGORY_LIQUIFIER);
        LIQUIFIER_ENERGY_CAPACITY = COMMON_BUILDER.comment("Maximum energy capacity of the Liquifier").comment("Default: 100000 (100k)")
                .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
        LIQUIFIER_ENERGY_PER_TICK = COMMON_BUILDER.comment("Energy consumed per tick when Liquifier is processing").comment("Default: 50")
                .defineInRange("energyPerTick", 50, 0, Integer.MAX_VALUE);
        LIQUIFIER_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Ticks per operation when using the Liquifier").comment("Default: 100")
                .defineInRange("ticksPerOperation", 100, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Fission").push(CATEGORY_FISSION);
        FISSION_ENERGY_CAPACITY = COMMON_BUILDER.comment("Maximum energy capacity of the Fission multiblock").comment("Default: 100000 (100k)")
                .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
        FISSION_ENERGY_PER_TICK = COMMON_BUILDER.comment("Energy consumed per tick when the Fission multiblock is processing").comment("Default: 300")
                .defineInRange("energyPerTick", 300, 0, Integer.MAX_VALUE);
        FISSION_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Ticks per operation when using the Fission multiblock").comment("Default: 40")
                .defineInRange("ticksPerOperation", 40, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Fusion").push(CATEGORY_FUSION);
        FUSION_ENERGY_CAPACITY = COMMON_BUILDER.comment("Maximum energy capacity of the Fusion multiblock").comment("Default: 100000 (100k)")
                .defineInRange("energyCapacity", 100000, 0, Integer.MAX_VALUE);
        FUSION_ENERGY_PER_TICK = COMMON_BUILDER.comment("Energy consumed per tick when the Fusion multiblock is processing").comment("Default: 300")
                .defineInRange("energyPerTick", 300, 0, Integer.MAX_VALUE);
        FUSION_TICKS_PER_OPERATION = COMMON_BUILDER.comment("Ticks per operation when using the Fusion multiblock").comment("Default: 40")
                .defineInRange("ticksPerOperation", 40, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
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
