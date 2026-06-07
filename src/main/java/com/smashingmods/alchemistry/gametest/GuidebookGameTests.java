package com.smashingmods.alchemistry.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * In-world validation of the Patchouli guidebook's item references -- the in-world half of P4.alchemistry.6 (the
 * other half is the {@code guidebook_allParse} Tier-0 JUnit test, which proves every book JSON parses). This must
 * run in-world rather than as a unit test because the references resolve against {@link BuiltInRegistries#ITEM},
 * which only carries the ChemLib -> AlchemyLib -> Alchemistry chain once the server has loaded it; a unit JVM has
 * only vanilla after {@code Bootstrap} (OQ-2).
 *
 * <p>Like the other holders this class lives in {@code src/main} so the mod scan registers it for the
 * {@code gameTestServer} run, but the {@code jar}/{@code sourcesJar}/{@code javadoc} tasks exclude the
 * {@code gametest} package so it never ships. The single test is {@code required=false} so it can never gate the
 * {@code gameTestServer} exit code; the lone {@code required=true} gate stays the full-chain load-smoke in
 * {@link AlchemistryGameTests}. It pins {@code template = "loadsemptytemplate"} with
 * {@code @PrefixGameTestTemplate(false)} (the staged 3x3x3 air structure, id resolved un-prefixed to
 * {@code alchemistry:loadsemptytemplate}) like the other data-only checks; nothing is placed in-world, the
 * template just gives the framework a structure to run against. Bodies stay as thin plain helpers -- forward-compat
 * for the Phase-10 (1.21.5) gametest rewrite.</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class GuidebookGameTests {

    // The two classpath roots the book spans: localized content (categories + entries) under assets/, the book
    // definition under data/. Both are swept; the refs all live in the content files but the data root is walked
    // too so a future icon on the book definition would still be covered.
    private static final String CONTENT_ROOT = "assets/alchemistry/patchouli_books/alchemistry_book/en_us";
    private static final String BOOK_ROOT = "data/alchemistry/patchouli_books/alchemistry_book";

    // Patchouli fields that hold an item reference. icon is on categories/entries and spotlight pages; item is on
    // spotlight pages. The recipe field is deliberately NOT here -- it is a RECIPE id (resolved against recipes,
    // not BuiltInRegistries.ITEM), so treating it as an item ref would be a false failure (audit U1/U2).
    private static final List<String> ITEM_REF_KEYS = List.of("icon", "item");

    /**
     * Reads every guidebook JSON off the classpath, extracts each {@code icon}/{@code item} reference, and asserts
     * the parsed item id is a registered item. Fails on the first unresolved ref (with the file and id), otherwise
     * succeeds; reports the number of refs checked. Because this runs against a fully-booted server the ChemLib and
     * Alchemistry items are registered, so the machine/block icons resolve via their {@code BlockItem}s.
     */
    @GameTest(required = false, template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void guidebook_itemRefsResolve(GameTestHelper helper) {
        List<Path> jsonFiles;
        try {
            jsonFiles = new ArrayList<>();
            jsonFiles.addAll(walkJsonResources(CONTENT_ROOT));
            jsonFiles.addAll(walkJsonResources(BOOK_ROOT));
        } catch (IOException | URISyntaxException e) {
            helper.fail("could not enumerate guidebook JSON resources: " + e);
            throw new IllegalStateException("unreachable -- helper.fail throws", e);
        }

        int checked = 0;
        for (Path file : jsonFiles) {
            JsonElement root;
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                root = JsonParser.parseReader(reader);
            } catch (Exception e) {
                // Parse failures are the Tier-0 test's job; surface here too so this never silently skips a file.
                helper.fail(file.getFileName() + ": failed to parse -- " + e.getMessage());
                throw new IllegalStateException("unreachable -- helper.fail throws", e);
            }

            List<String> refs = new ArrayList<>();
            collectItemRefs(root, refs);
            for (String ref : refs) {
                ResourceLocation id = parseItemId(ref);
                if (id == null) {
                    // An exotic icon form we do not recognise -- report rather than silently pass it.
                    helper.fail(file.getFileName() + ": unparseable item ref \"" + ref + "\"");
                }
                if (!BuiltInRegistries.ITEM.containsKey(id)) {
                    helper.fail(file.getFileName() + ": unresolved item ref " + id);
                }
                checked++;
            }
        }

        System.out.println("[GuidebookGameTests] guidebook_itemRefsResolve checked " + checked
                + " item refs across " + jsonFiles.size() + " files -- all resolved");
        helper.succeed();
    }

    // Recursively collects the string values of every icon/item key anywhere in the JSON tree. A recursive walk is
    // used because icon sits at the top level of categories/entries while item sits inside pages[]; collecting only
    // the ITEM_REF_KEYS values naturally skips recipe ids and the multiblock mapping (keyed by glyphs like "X").
    private static void collectItemRefs(JsonElement element, List<String> into) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                JsonElement value = entry.getValue();
                if (ITEM_REF_KEYS.contains(entry.getKey()) && value.isJsonPrimitive()
                        && value.getAsJsonPrimitive().isString()) {
                    into.add(value.getAsString());
                } else {
                    collectItemRefs(value, into);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement child : array) {
                collectItemRefs(child, into);
            }
        }
    }

    // Parses the item id out of a Patchouli item-stack string. The common forms are "modid:item",
    // "modid:item 4" (trailing count), "modid:item#nbt" and "modid:item{nbt}" (NBT suffix); the count and NBT are
    // stripped before parsing the id. Returns null for a form that does not yield a valid ResourceLocation so the
    // caller can report it rather than silently pass.
    private static ResourceLocation parseItemId(String ref) {
        String id = ref.trim();
        // Strip a trailing count (e.g. "modid:item 4") -- the id itself never contains a space.
        int space = id.indexOf(' ');
        if (space >= 0) {
            id = id.substring(0, space);
        }
        // Strip an NBT suffix in either the #nbt or {nbt} form.
        int hash = id.indexOf('#');
        if (hash >= 0) {
            id = id.substring(0, hash);
        }
        int brace = id.indexOf('{');
        if (brace >= 0) {
            id = id.substring(0, brace);
        }
        return ResourceLocation.tryParse(id.trim());
    }

    // Walks a classpath directory for *.json. The gameTestServer run always uses the exploded-directory (file:)
    // layout under build/resources/main, so in practice only the file: branch below executes. The jar: branch is
    // defensive cover should the book ever be run from a packaged jar -- correct, but UNTESTED here. Mirrors the
    // enumeration in the Tier-0 GuidebookParseTest so both tiers sweep the same files.
    private static List<Path> walkJsonResources(String classpathDir) throws IOException, URISyntaxException {
        List<Path> result = new ArrayList<>();
        for (URL url : Collections.list(GuidebookGameTests.class.getClassLoader().getResources(classpathDir))) {
            URI uri = url.toURI();
            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fs = openJarFileSystem(uri)) {
                    collectJson(fs.getPath(classpathDir), result);
                }
            } else {
                collectJson(Paths.get(uri), result);
            }
        }
        return result;
    }

    private static void collectJson(Path dir, List<Path> into) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .forEach(into::add);
        }
    }

    // A jar: URI needs its FileSystem opened before getPath works; reuse one already open for the same jar.
    private static FileSystem openJarFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Map.of());
        }
    }
}
