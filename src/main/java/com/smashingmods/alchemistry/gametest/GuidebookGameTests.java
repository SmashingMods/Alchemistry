package com.smashingmods.alchemistry.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
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
 * In-world validation of the Modonomicon guidebook: that its item references resolve to registered items, and
 * that Modonomicon actually ingested the book (the companion {@code guidebook_allParse} JUnit test proves every
 * book JSON parses). Both checks must run in-world rather than as unit tests -- the refs resolve against
 * {@link BuiltInRegistries#ITEM}, which only carries the ChemLib -> AlchemyLib -> Alchemistry chain once the
 * server has loaded it (a unit JVM has only vanilla after {@code Bootstrap}), and the book is built by
 * Modonomicon's server datapack-reload listener, which only runs in a booted server.
 *
 * <p>Like the other holders this class lives in {@code src/main} so the mod scan registers it for the
 * {@code gameTestServer} run, but the {@code jar}/{@code sourcesJar}/{@code javadoc} tasks exclude the
 * {@code gametest} package so it never ships. Both tests are {@code required=true} (the {@code @GameTest}
 * default), so a failure fails the {@code gameTestServer} gate alongside the full-chain load-smoke in
 * {@link AlchemistryGameTests}. They pin {@code template = "loadsemptytemplate"} with
 * {@code @PrefixGameTestTemplate(false)} (the staged 3x3x3 air structure, id resolved un-prefixed to
 * {@code alchemistry:loadsemptytemplate}) like the other data-only checks; nothing is placed in-world, the
 * template just gives the framework a structure to run against. Bodies stay as thin plain helpers so the
 * {@code @GameTest} methods stay thin.</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class GuidebookGameTests {

    // The two classpath roots the book spans, both under data/: the book definition + categories + entries under
    // books/, and the multiblock structure definitions under the separate multiblocks/ tree. Both are swept; the
    // item refs all live in the book tree, but the multiblock root is walked too so the sweep mirrors
    // GuidebookParseTest -- its block matchers key on block/display/tag (not item), so they are naturally skipped.
    private static final String BOOK_ROOT = "data/alchemistry/modonomicon/books/alchemistry_book";
    private static final String MULTIBLOCK_ROOT = "data/alchemistry/modonomicon/multiblocks";

    // The book id Modonomicon derives from data/alchemistry/modonomicon/books/alchemistry_book/book.json: the
    // namespace plus the book folder name. This must match BookDataManager's loaded-book key (and the book_id
    // component the book-grant recipe stamps onto the Modonomicon item).
    private static final ResourceLocation BOOK_ID =
            ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "alchemistry_book");

    // The Modonomicon field that holds an item reference. icon (on categories/entries) is a BookIconModel object
    // and spotlight pages carry an item ItemStack object; both nest the id under an "item" string. recipe_id_1 (a
    // RECIPE id) and multiblock_id (a multiblock def ref) are deliberately NOT here -- they resolve against the
    // recipe and multiblock registries, not BuiltInRegistries.ITEM, so treating them as item refs would be a false
    // failure. Keyed on the leaf field name so collecting it skips both naturally.
    private static final String ITEM_REF_KEY = "item";

    /**
     * Reads every guidebook JSON off the classpath, extracts each {@code item} reference (the id nested in an
     * {@code icon} {@code BookIconModel} or a spotlight page's {@code item} stack), and asserts the parsed item id
     * is a registered item. Fails on the first unresolved ref (with the file and id), otherwise succeeds; reports
     * the number of refs checked. Because this runs against a fully-booted server the ChemLib and Alchemistry items
     * are registered, so the machine/block icons resolve via their {@code BlockItem}s.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void guidebook_itemRefsResolve(GameTestHelper helper) {
        List<Path> jsonFiles;
        try {
            jsonFiles = new ArrayList<>();
            jsonFiles.addAll(walkJsonResources(BOOK_ROOT));
            jsonFiles.addAll(walkJsonResources(MULTIBLOCK_ROOT));
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
                // Parse failures are the GuidebookParseTest's job; surface here too so this never silently skips a file.
                helper.fail(file.getFileName() + ": failed to parse -- " + e.getMessage());
                throw new IllegalStateException("unreachable -- helper.fail throws", e);
            }

            List<String> refs = new ArrayList<>();
            collectItemRefs(root, refs);
            for (String ref : refs) {
                ResourceLocation id = parseItemId(ref);
                if (id == null) {
                    // An exotic item form we do not recognise -- report rather than silently pass it.
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

    /**
     * Asserts Modonomicon actually loaded the book and that it builds with its categories and entries attached.
     * The parse and ref-resolve checks are blind to whether Modonomicon ingested the book at all -- the book def
     * could be deleted or renamed and they would still pass. Modonomicon registers its {@link BookDataManager} as a
     * server datapack-reload listener, so by the time a gametest runs the book JSON is parsed: the book is keyed by
     * id, its categories are attached to it, and each entry is attached to its category.
     *
     * <p>Building the book is a separate, later step. Modonomicon only copies a category's entries up into
     * {@link Book#getEntries()} in {@code Book.build()}, and on the server it runs that build lazily from its
     * {@code OnDatapackSyncEvent} handler -- which fires on player join or {@code /reload}. A {@code gameTestServer}
     * run boots a dedicated server with no player ever joining and never runs {@code /reload}, so the build is never
     * triggered and {@link Book#getEntries()} stays empty even though every entry parsed and is sitting on its
     * category. This test therefore drives the build itself via {@link BookDataManager#tryBuildBooks(Level)} -- the
     * exact entry point the player-join handler calls -- so it exercises the real server build path before asserting
     * (the call is idempotent: {@code tryBuildBooks} no-ops once the books are built).</p>
     *
     * <p>A bare {@code getBook != null} check under-reaches: {@code BookDataManager.apply()} keys the book by id as
     * soon as its {@code book.json} parses, before attaching categories and entries, and a malformed category or
     * entry is logged and skipped without removing the key. A book corrupt below {@code book.json} -- a bad page
     * type, a dangling parent, a missing required field -- therefore still registers its key and would pass a
     * presence-only check while loading empty or broken in-game. So this also asserts the built {@link Book} has at
     * least one category and at least one entry (a category or entry that fails to parse outright is never added to
     * the book, so an all-broken book has an empty map and fails here). Counts are deliberately not asserted, so
     * adding categories or entries does not break the gate. Renaming or removing the book definition drops the key
     * and fails the presence check.</p>
     *
     * <p>The non-empty checks still under-reach on <em>partial</em> corruption, though: {@link Book#build(Level)}
     * copies every entry up into {@link Book#getEntries()} first and validates afterwards, so an entry with a
     * dangling parent, a bad page, or a missing {@code entryToOpen} stays in the map (the non-empty check passes)
     * while recording a {@link BookErrorManager} error. The parse phase ({@code apply()}) resets the error store on
     * each reload, so a parse error would not survive for a later query -- but the build phase this test drives via
     * {@link BookDataManager#tryBuildBooks(Level)} does not: {@code buildBooks} resets only the context helper, not
     * the per-book error store, so a build error is recorded against {@link #BOOK_ID} and is still queryable here.
     * So after the non-empty checks this also fails if {@code BookErrorManager} recorded any build error for the
     * book, catching corruption the count checks would otherwise wave through. The clean book records no errors, so
     * this passes today.</p>
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void guidebook_bookLoads(GameTestHelper helper) {
        Book book = BookDataManager.get().getBook(BOOK_ID);
        if (book == null) {
            helper.fail("Modonomicon did not load the guidebook " + BOOK_ID
                    + "; loaded books: " + BookDataManager.get().getBooks().keySet());
        }
        if (book.getCategories().isEmpty()) {
            helper.fail("Modonomicon loaded the guidebook " + BOOK_ID
                    + " with no categories -- a category failed to parse and was skipped");
        }

        // The dedicated gameTestServer has no player join to trigger Modonomicon's lazy server-side book build, so
        // drive it here the way the player-join handler does; without this Book.getEntries() is always empty.
        BookDataManager.get().tryBuildBooks(helper.getLevel());

        if (book.getEntries().isEmpty()) {
            helper.fail("Modonomicon built the guidebook " + BOOK_ID
                    + " with no entries -- an entry failed to parse and was skipped");
        }

        // The non-empty checks above pass on partially-corrupt entries (Book.build copies entries up before
        // validating them), but the build records the corruption as a BookErrorManager error keyed to the book and
        // buildBooks does not reset that store, so the error survives for this query.
        if (BookErrorManager.get().hasErrors(BOOK_ID)) {
            helper.fail("Modonomicon recorded build errors for " + BOOK_ID + ": "
                    + BookErrorManager.get().getErrors(BOOK_ID).getErrors());
        }

        System.out.println("[GuidebookGameTests] guidebook_bookLoads confirmed " + BOOK_ID + " is loaded with "
                + book.getCategories().size() + " categories and " + book.getEntries().size() + " entries");
        helper.succeed();
    }

    // Recursively collects the string values of every "item" key anywhere in the JSON tree. A recursive walk is
    // used because the id sits at differing depths -- the item inside a category/entry icon object, and the item
    // inside a spotlight page's item stack object; collecting only the ITEM_REF_KEY value naturally skips recipe
    // ids, multiblock ids, and the multiblock block/display/tag matchers.
    private static void collectItemRefs(JsonElement element, List<String> into) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                JsonElement value = entry.getValue();
                if (ITEM_REF_KEY.equals(entry.getKey()) && value.isJsonPrimitive()
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

    // Parses the item id out of a Modonomicon item reference. Modonomicon nests a plain item-id ResourceLocation
    // under the "item" key (the count, when present, is a sibling field, not a suffix), so the value parses
    // directly. Returns null for a form that does not yield a valid ResourceLocation so the caller can report it
    // rather than silently pass.
    private static ResourceLocation parseItemId(String ref) {
        return ResourceLocation.tryParse(ref.trim());
    }

    // Walks a classpath directory for *.json. The gameTestServer run uses the exploded-directory (file:)
    // layout under build/resources/main, so the file: branch below executes; the jar: branch covers the book
    // being run from a packaged jar. Mirrors the enumeration in GuidebookParseTest so both sweep the same files.
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
