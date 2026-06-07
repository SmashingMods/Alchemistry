package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * In-game test holder for Alchemistry. The {@code gameTestServer} run boots a dedicated server, loads every
 * {@code @GameTestHolder} from the enabled namespaces, runs the tests against their structure templates, and
 * exits non-zero if any fails. This class is compiled into {@code src/main} so the mod scan discovers it, but
 * the {@code jar} task excludes the {@code gametest} package so the tests never ship in the published jar.
 *
 * <p>The lone test here is a harness probe: it proves the gametest layer actually runs on this toolchain
 * (NeoGradle 7 / NeoForge 1.20.2) with the full ChemLib -> AlchemyLib -> Alchemistry chain loaded. The
 * behaviour assertions for the machines arrive in later Phase-4 tickets.</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class AlchemistryGameTests {

    /**
     * Loads the empty 3x3x3 template and immediately succeeds -- a smoke test that the structure resolves and
     * the test framework completes a pass. {@code @PrefixGameTestTemplate(false)} keeps the template name
     * un-prefixed, so the framework looks for {@code alchemistry:loadsemptytemplate} and reads the staged
     * {@code gameteststructures/loadsemptytemplate.snbt} from the run's working directory.
     */
    @GameTest
    @PrefixGameTestTemplate(false)
    public void loadsEmptyTemplate(GameTestHelper helper) {
        succeedImmediately(helper);
    }

    // Body kept as a plain helper so the @GameTest methods stay thin -- forward-compat for the Phase-10
    // (1.21.5) gametest rewrite, where the per-machine assertions land in helpers like this one.
    private static void succeedImmediately(GameTestHelper helper) {
        helper.succeed();
    }
}
