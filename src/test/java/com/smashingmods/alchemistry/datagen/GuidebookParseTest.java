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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tier-0 test: every Patchouli guidebook JSON parses. No bootstrap needed -- this is pure Gson parsing, so it
 * stays off the Minecraft classpath entirely (unlike {@link com.smashingmods.alchemistry.testsupport.BootstrappedTest}
 * subclasses). The companion {@code guidebook_itemRefsResolve} gametest does the in-world half: that the
 * {@code icon}/{@code item} refs resolve to registered items, which needs the full mod chain loaded (per OQ-2 the
 * registry is only populated in-world, so a unit JVM with just vanilla after {@code Bootstrap} cannot check it).
 *
 * <p>The book ships across two resource roots -- the localized content (categories + entries) under
 * {@code assets/alchemistry/patchouli_books/...} and the book definition under
 * {@code data/alchemistry/patchouli_books/...} -- so both are swept. The files are read from the classpath
 * (Gradle's {@code test} task puts {@code build/resources/main} on the runtime classpath), and the roots are
 * walked rather than a count hardcoded, so an added or removed entry is picked up automatically; the count is
 * asserted as a floor and reported.</p>
 */
class GuidebookParseTest {

    // The two classpath directories the book spans. en_us is the only shipped locale; the book.json lives under
    // data/, the localized categories/entries under assets/ -- both are walked recursively for *.json.
    private static final String CONTENT_ROOT = "assets/alchemistry/patchouli_books/alchemistry_book/en_us";
    private static final String BOOK_ROOT = "data/alchemistry/patchouli_books/alchemistry_book";

    // 16 content files (6 categories + 10 entries) + 1 book.json. Asserted as a floor so adding a page never
    // breaks the test, but the exact count is reported so a regression that drops files is still visible.
    private static final int EXPECTED_FILE_COUNT = 17;

    @Test
    void guidebook_allParse() throws IOException, URISyntaxException {
        List<Path> jsonFiles = new ArrayList<>();
        jsonFiles.addAll(walkJsonResources(CONTENT_ROOT));
        jsonFiles.addAll(walkJsonResources(BOOK_ROOT));

        assertTrue(jsonFiles.size() >= EXPECTED_FILE_COUNT,
                "expected at least " + EXPECTED_FILE_COUNT + " guidebook JSON files, found " + jsonFiles.size()
                        + ": " + jsonFiles);

        for (Path file : jsonFiles) {
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonParser.parseReader(reader);
            } catch (Exception e) {
                fail("guidebook JSON did not parse: " + file + " -- " + e.getMessage(), e);
            }
        }

        System.out.println("[GuidebookParseTest] parsed " + jsonFiles.size() + " guidebook JSON files");
    }

    // Walks a classpath directory for *.json, handling both the exploded-directory (file:) layout Gradle's test
    // task uses and the jar: layout a packaged run would use. Each ClassLoader root that contains the directory is
    // walked, so a split across resource roots is covered.
    private static List<Path> walkJsonResources(String classpathDir) throws IOException, URISyntaxException {
        List<Path> result = new ArrayList<>();
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
