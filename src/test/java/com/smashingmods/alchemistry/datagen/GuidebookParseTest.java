package com.smashingmods.alchemistry.datagen;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tier-0 test: every Modonomicon guidebook JSON parses. No bootstrap needed -- this is pure Gson parsing, so it
 * stays off the Minecraft classpath entirely (unlike {@link com.smashingmods.alchemistry.testsupport.BootstrappedTest}
 * subclasses). The companion {@code guidebook_itemRefsResolve} gametest does the in-world half: that the book's
 * item references resolve to registered items, which needs the full mod chain loaded (per OQ-2 the registry is
 * only populated in-world, so a unit JVM with just vanilla after {@code Bootstrap} cannot check it); the
 * companion {@code guidebook_bookLoads} gametest proves Modonomicon actually ingested the book.
 *
 * <p>Modonomicon keeps the whole book under {@code data/} -- the book definition, categories and entries under
 * {@code data/alchemistry/modonomicon/books/alchemistry_book/}, and the multiblock structure definitions under
 * the separate {@code data/alchemistry/modonomicon/multiblocks/} tree -- so both roots are swept. The files are
 * read from the classpath (Gradle's {@code test} task puts {@code build/resources/main} on the runtime classpath,
 * and {@code src/generated/resources} is wired into the main resource set, so the datagen-authored book lands
 * there), and the roots are walked rather than a count hardcoded, so an added or removed entry is picked up
 * automatically; the swept set is asserted to equal the expected consolidated structure and the count is reported.</p>
 */
class GuidebookParseTest {

    // The two classpath directories the book spans, both under data/: the book definition + categories + entries
    // under books/, and the multiblock structure definitions under the separate multiblocks/ tree. Both are walked
    // recursively for *.json.
    private static final String BOOK_ROOT = "data/alchemistry/modonomicon/books/alchemistry_book";
    private static final String MULTIBLOCK_ROOT = "data/alchemistry/modonomicon/multiblocks";

    // The exact set of guidebook JSON files the datagen authors, each keyed by its path relative to its classpath
    // root above (forward-slashed). The book is one book.json + 2 categories + 10 entries + 2 multiblock definitions:
    // the six former per-machine categories were consolidated into a single machines_category (reactor_category
    // stays), so the machine entries all live under entries/machines_category/. Asserting the whole set -- rather
    // than a count floor -- means a dropped or renamed file fails the test even if some other file is added in its
    // place, while an added file is reported as an unexpected extra rather than silently absorbed.
    private static final Set<String> EXPECTED_BOOK_FILES = Set.of(
            "book.json",
            "categories/machines_category.json",
            "categories/reactor_category.json",
            "entries/machines_category/atomizer.json",
            "entries/machines_category/combiner.json",
            "entries/machines_category/compactor.json",
            "entries/machines_category/dissolver.json",
            "entries/machines_category/liquifier.json",
            "entries/reactor_category/fission.json",
            "entries/reactor_category/fission_multiblock.json",
            "entries/reactor_category/fusion.json",
            "entries/reactor_category/fusion_multiblock.json",
            "entries/reactor_category/reactor.json");
    private static final Set<String> EXPECTED_MULTIBLOCK_FILES = Set.of(
            "fission_reactor.json",
            "fusion_reactor.json");

    @Test
    void guidebook_allParse() throws IOException, URISyntaxException {
        Map<String, Path> bookFiles = walkJsonResources(BOOK_ROOT);
        Map<String, Path> multiblockFiles = walkJsonResources(MULTIBLOCK_ROOT);

        assertEquals(EXPECTED_BOOK_FILES, bookFiles.keySet(),
                "guidebook book/category/entry files differ from the consolidated structure");
        assertEquals(EXPECTED_MULTIBLOCK_FILES, multiblockFiles.keySet(),
                "guidebook multiblock definition files differ from the consolidated structure");

        List<Path> jsonFiles = new ArrayList<>();
        jsonFiles.addAll(bookFiles.values());
        jsonFiles.addAll(multiblockFiles.values());

        for (Path file : jsonFiles) {
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonParser.parseReader(reader);
            } catch (Exception e) {
                fail("guidebook JSON did not parse: " + file + " -- " + e.getMessage(), e);
            }
        }

        System.out.println("[GuidebookParseTest] parsed " + jsonFiles.size() + " guidebook JSON files");
    }

    // Walks a classpath directory for *.json, keyed by each file's forward-slashed path relative to that directory
    // (so the keys can be matched against the EXPECTED_* sets regardless of the absolute root or the OS separator).
    // Gradle's test task always runs against the exploded-directory (file:) layout under build/resources/main, so in
    // practice only the file: branch below executes. The jar: branch is defensive cover should the book ever be run
    // from a packaged jar -- correct, but UNTESTED here. Each ClassLoader root that contains the directory is walked,
    // so a split across resource roots is covered.
    private static Map<String, Path> walkJsonResources(String classpathDir) throws IOException, URISyntaxException {
        Map<String, Path> result = new HashMap<>();
        for (URL url : Collections.list(GuidebookParseTest.class.getClassLoader().getResources(classpathDir))) {
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

    private static void collectJson(Path dir, Map<String, Path> into) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> into.put(relativeKey(dir, p), p));
        }
    }

    // The file's path relative to the walked directory, forward-slashed so keys are stable across resource roots
    // and operating systems.
    private static String relativeKey(Path dir, Path file) {
        return dir.relativize(file).toString().replace('\\', '/');
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
