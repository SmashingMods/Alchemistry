package com.smashingmods.alchemistry.datagen;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tier-0 test: every Alchemistry block has an {@code assets/alchemistry/items/<id>.json} client-item definition on
 * the classpath. Since 1.21.4 a block-item with no such definition renders as the missing-model checkerboard, and
 * the compactor is excluded from {@link BlockStateGenerator} (its blockstate, model and item definition are authored
 * by hand), so datagen never emits one for it -- this would have caught the compactor falling back to the missing
 * texture. The other machines' definitions are datagen-emitted under {@code src/generated/resources} (wired into the
 * main resource set), so the one sweep covers both the hand-authored and the generated definitions.
 *
 * <p>No bootstrap needed -- this is classpath resource existence plus a Gson parse, so it stays off the Minecraft
 * classpath like {@link GuidebookParseTest}. Gradle's {@code test} task puts {@code build/resources/main} (the
 * processed main + generated resources) on the runtime classpath, so the definitions are read from there.</p>
 */
class ItemModelDefinitionTest {

    // The path under assets/alchemistry/items/ that each block ItemRegistry.fromBlock registers an item for: the
    // five machines, both chamber controllers and the reactor parts. Asserting the whole set -- the compactor first,
    // as the regression case -- means a block that loses or never gains its item definition fails the test. Kept in
    // step with BlockRegistry by hand, mirroring GuidebookParseTest's EXPECTED_* sets.
    private static final List<String> BLOCK_ITEM_IDS = List.of(
            "compactor",
            "atomizer",
            "combiner",
            "dissolver",
            "liquifier",
            "fission_chamber_controller",
            "fusion_chamber_controller",
            "fission_core",
            "fusion_core",
            "reactor_casing",
            "reactor_glass",
            "reactor_energy",
            "reactor_input",
            "reactor_output");

    @Test
    void everyBlockHasAnItemModelDefinition() {
        for (String id : BLOCK_ITEM_IDS) {
            String resource = "assets/alchemistry/items/" + id + ".json";
            try (InputStream in = ItemModelDefinitionTest.class.getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(in, () -> "missing client-item definition " + resource
                        + " -- the block-item renders as the missing-model checkerboard without it");
                try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8))) {
                    JsonParser.parseReader(reader);
                }
            } catch (IOException e) {
                fail("could not read item-model definition " + resource + " -- " + e.getMessage(), e);
            } catch (Exception e) {
                fail("item-model definition did not parse: " + resource + " -- " + e.getMessage(), e);
            }
        }

        System.out.println("[ItemModelDefinitionTest] verified " + BLOCK_ITEM_IDS.size() + " item-model definitions");
    }
}
