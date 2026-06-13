package com.smashingmods.alchemistry.common.block;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlock;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlock;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlock;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlock;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlock;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlock;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlock;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 pin for the machines' energy-requirement tooltip. The broken form built the line as
 * {@code new TranslatableContents(key, String.valueOf(config), NO_ARGS)} -- which puts the FE/t value in the
 * FALLBACK slot and leaves {@code getArgs()} empty -- so the lang's {@code "Requires %d FE/t"} rendered with the
 * placeholder unfilled. The fix is {@code Component.translatable(key, config)}, which puts the value in
 * {@code getArgs()} where the {@code %d} reads it.
 *
 * <p>This asserts the structural contract that distinguishes the two without a loaded language: the energy line is
 * a {@link TranslatableContents} for the {@code energy_requirement} key whose {@code getArgs()} is non-empty and
 * carries the configured FE/t value. The broken form fails it twice over (empty args; value only in the fallback).</p>
 *
 * <p>The block-level {@link TooltipBlock#appendHoverText} is invoked directly (it is what {@link AlchemistryBlockItem}
 * forwards to in-game), so no item registration or render pass is involved. {@code TooltipDisplay} is passed as
 * {@code null} because these blocks never read it, and the stack is {@link ItemStack#EMPTY} for the same reason.
 * Constructing a block after {@code Bootstrap} would hit the frozen block registry's intrusive-holder write, so the
 * block and item registries are unfrozen first (tags preserved); these blocks are never registered, so this only
 * permits the construction and leaves the bootstrapped entries intact. The machines read
 * {@code Config.Common.*EnergyPerTick.get()}, which throws until a config is loaded, so the cache of each value is
 * primed to a known sentinel through the same kind of reflection {@link BootstrappedTest} uses for the bootstrap.</p>
 */
class EnergyRequirementTooltipTest extends BootstrappedTest {

    private static final String ENERGY_REQUIREMENT_KEY = "tooltip.alchemistry.energy_requirement";

    // Distinct primed FE/t values per machine, so a tooltip that reads the wrong machine's config would also fail.
    private static final int ATOMIZER_FE = 11;
    private static final int COMBINER_FE = 22;
    private static final int COMPACTOR_FE = 33;
    private static final int DISSOLVER_FE = 44;
    private static final int FISSION_FE = 55;
    private static final int FUSION_FE = 66;
    private static final int LIQUIFIER_FE = 77;

    @BeforeAll
    static void prepareConfigAndRegistries() {
        // Touch a Config member so the outer class initialises: the per-machine IntValues are assigned in the Common
        // constructor that Config's static block runs, and initialising the nested Common alone would not run it.
        assertInstanceOf(ModConfigSpec.class, Config.COMMON_SPEC, "Config must initialise before priming its values");

        unfreeze(BuiltInRegistries.BLOCK);
        unfreeze(BuiltInRegistries.ITEM);

        primeConfigValue(Config.Common.atomizerEnergyPerTick, ATOMIZER_FE);
        primeConfigValue(Config.Common.combinerEnergyPerTick, COMBINER_FE);
        primeConfigValue(Config.Common.compactorEnergyPerTick, COMPACTOR_FE);
        primeConfigValue(Config.Common.dissolverEnergyPerTick, DISSOLVER_FE);
        primeConfigValue(Config.Common.fissionEnergyPerTick, FISSION_FE);
        primeConfigValue(Config.Common.fusionEnergyPerTick, FUSION_FE);
        primeConfigValue(Config.Common.liquifierEnergyPerTick, LIQUIFIER_FE);
    }

    @Test
    void atomizer_energyTooltipFeedsTheArg() {
        assertEnergyArg(new AtomizerBlock(machineProperties("atomizer")), ATOMIZER_FE);
    }

    @Test
    void combiner_energyTooltipFeedsTheArg() {
        assertEnergyArg(new CombinerBlock(machineProperties("combiner")), COMBINER_FE);
    }

    @Test
    void compactor_energyTooltipFeedsTheArg() {
        assertEnergyArg(new CompactorBlock(machineProperties("compactor")), COMPACTOR_FE);
    }

    @Test
    void dissolver_energyTooltipFeedsTheArg() {
        assertEnergyArg(new DissolverBlock(machineProperties("dissolver")), DISSOLVER_FE);
    }

    @Test
    void fissionController_energyTooltipFeedsTheArg() {
        assertEnergyArg(new FissionControllerBlock(machineProperties("fission_chamber_controller")), FISSION_FE);
    }

    @Test
    void fusionController_energyTooltipFeedsTheArg() {
        assertEnergyArg(new FusionControllerBlock(machineProperties("fusion_chamber_controller")), FUSION_FE);
    }

    @Test
    void liquifier_energyTooltipFeedsTheArg() {
        assertEnergyArg(new LiquifierBlock(machineProperties("liquifier")), LIQUIFIER_FE);
    }

    // The standard machine properties with the block id set, which BlockBehaviour's constructor needs (it resolves
    // the loot table off the id). The id picks no behaviour the tooltip depends on -- it only lets construction past
    // the same requireNonNull the real registration satisfies.
    private static BlockBehaviour.Properties machineProperties(String path) {
        return AbstractProcessingBlock.machineProperties()
                .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alchemistry", path)));
    }

    // Collects the block's tooltip lines, finds the energy_requirement line, and asserts it is a TranslatableContents
    // whose args carry the configured FE/t value (rather than NO_ARGS with the value stranded in the fallback slot).
    private static void assertEnergyArg(Block block, int expectedFePerTick) {
        TranslatableContents energyLine = energyRequirementLine(block);
        assertTrue(energyLine.getArgs().length > 0,
                "energy tooltip must pass the FE/t value as a format arg, not NO_ARGS with it in the fallback");
        assertTrue(List.of(energyLine.getArgs()).contains(expectedFePerTick),
                () -> "energy tooltip args must contain the configured FE/t value " + expectedFePerTick
                        + ", got " + List.of(energyLine.getArgs()));
    }

    private static TranslatableContents energyRequirementLine(Block block) {
        assertInstanceOf(TooltipBlock.class, block, "machine block must contribute a tooltip line");
        List<Component> tooltip = new ArrayList<>();
        Consumer<Component> adder = tooltip::add;
        ((TooltipBlock) block).appendHoverText(ItemStack.EMPTY, Item.TooltipContext.EMPTY, null, adder, TooltipFlag.NORMAL);

        List<TranslatableContents> energyLines = tooltip.stream()
                .map(Component::getContents)
                .filter(TranslatableContents.class::isInstance)
                .map(TranslatableContents.class::cast)
                .filter(contents -> ENERGY_REQUIREMENT_KEY.equals(contents.getKey()))
                .toList();
        assertEquals(1, energyLines.size(),
                () -> "expected exactly one energy_requirement tooltip line, got " + energyLines.size());
        return energyLines.getFirst();
    }

    // Lifts the freeze a block's intrusive-holder constructor write needs. Bootstrap freezes the built-in registries;
    // unfreeze(false) clears only the frozen flag, leaving the bootstrapped entries and their tags in place.
    @SuppressWarnings("deprecation")
    private static void unfreeze(Registry<?> registry) {
        if (registry instanceof MappedRegistry<?> mappedRegistry) {
            mappedRegistry.unfreeze(false);
        }
    }

    // Primes a config value's cache so get() returns the sentinel without a loaded config. ModConfigSpec.ConfigValue
    // caches the value in a private field and returns it from get() when non-null; the value can only otherwise be
    // read once a config file is bound. Mirrors the reflective registry priming BootstrappedTest does for the same
    // "make a launch-time-only state available to a plain-JUnit JVM" reason.
    private static void primeConfigValue(ModConfigSpec.IntValue value, int primed) {
        try {
            Field cachedValue = ModConfigSpec.ConfigValue.class.getDeclaredField("cachedValue");
            cachedValue.setAccessible(true);
            cachedValue.set(value, primed);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to prime the energy-per-tick config value for the tooltip test", exception);
        }
    }
}
