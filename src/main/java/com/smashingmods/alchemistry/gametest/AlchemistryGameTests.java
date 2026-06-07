package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * In-game test holder for Alchemistry. The {@code gameTestServer} run boots a dedicated server, loads every
 * {@code @GameTestHolder} from the enabled namespaces, runs the tests against their structure templates, and
 * exits non-zero if any fails. This class is compiled into {@code src/main} so the mod scan discovers it, but
 * the {@code jar} task excludes the {@code gametest} package so the tests never ship in the published jar.
 *
 * <p>The lone test here is the standing port gate: a {@code required=true} load-smoke that asserts the full
 * ChemLib -> AlchemyLib -> Alchemistry chain registered before the server reached the in-world phase. Because
 * it is the only {@code required} test, a non-zero {@code gameTestServer} exit means the chain failed to load,
 * which is exactly the signal a Phase-5+ port wants. The richer per-machine behaviour tests land as
 * {@code required=false} in later Phase-4 tickets, so this stays the single must-pass gate.</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class AlchemistryGameTests {

    /**
     * Loads the empty 3x3x3 template and asserts the full mod chain registered, then succeeds. The registry
     * lookups are static and do not need the in-world structure, but running them inside a gametest proves the
     * checks execute against a fully-booted server with the chain loaded. {@code @PrefixGameTestTemplate(false)}
     * keeps the template name un-prefixed, and {@code template = "loadsemptytemplate"} pins it to the existing
     * staged structure (the framework would otherwise derive the name from this method), so it resolves
     * {@code alchemistry:loadsemptytemplate} and reads {@code gameteststructures/loadsemptytemplate.snbt} from
     * the run's working directory.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void fullChainLoaded(GameTestHelper helper) {
        assertChainLoaded(helper);
    }

    // Body kept as a plain helper so the @GameTest methods stay thin -- forward-compat for the Phase-10
    // (1.21.5) gametest rewrite, where the per-machine assertions land in helpers like this one.
    //
    // Each check is a static built-in registry lookup against a known id from one rung of the chain: a ChemLib
    // element (hydrogen, element #1, always present), an Alchemistry machine block, its block-entity type, and
    // its recipe type. A missing id means that rung never registered, so the gate fails the required test.
    private static void assertChainLoaded(GameTestHelper helper) {
        assertRegistered(helper, "chemlib element", BuiltInRegistries.ITEM,
                new ResourceLocation("chemlib", "hydrogen"));
        assertRegistered(helper, "alchemistry block", BuiltInRegistries.BLOCK,
                new ResourceLocation(Alchemistry.MODID, "dissolver"));
        assertRegistered(helper, "alchemistry block-entity type", BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Alchemistry.MODID, "dissolver_block_entity"));
        assertRegistered(helper, "alchemistry recipe type", BuiltInRegistries.RECIPE_TYPE,
                new ResourceLocation(Alchemistry.MODID, "dissolver"));
        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, String what, Registry<?> registry, ResourceLocation id) {
        if (!registry.containsKey(id)) {
            helper.fail(String.format("%s not registered: %s", what, id));
        }
    }
}
