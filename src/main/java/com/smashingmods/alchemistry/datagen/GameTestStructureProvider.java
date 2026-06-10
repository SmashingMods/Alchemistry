package com.smashingmods.alchemistry.datagen;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the all-air structure templates the gametests run inside. 1.21.5 dropped the old
 * {@code gameteststructures/*.snbt} working-directory source (which the build staged by hand) -- the registry-based
 * gametest framework loads a test's structure as an ordinary datapack structure via {@code StructureTemplateManager},
 * i.e. binary {@code .nbt} under {@code data/<namespace>/structure/}. This provider emits the two boxes the tests
 * need, so they ride the normal generated-resources path (and {@code clean}/{@code runData} regenerates them) instead
 * of being staged by a one-off Gradle copy.
 *
 * <ul>
 *   <li>{@code loadsemptytemplate} -- a 3x3x3 air box for the single-block machine tests and the data-only checks
 *       (guidebook, recipe resolution), which only need a structure to run against;</li>
 *   <li>{@code reactor_space} -- a 9x9x9 air box with room for the 5x5x5 fission-reactor shell plus margin.</li>
 * </ul>
 *
 * <p>Each template is an empty {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate}
 * of the given size: a single-entry air palette plus two air blocks at opposite corners to pin the size, matching the
 * shape {@code StructureTemplate#save} produces. The framework clears and places the (all-air) structure, giving the
 * tests an empty region to build in. The {@code gametest} package that consumes these is excluded from the published
 * jar, so these structures are dev/test-only.</p>
 */
public class GameTestStructureProvider implements DataProvider {

    // The structure-pack directory and id-to-file convention StructureTemplateManager reads: data/<ns>/structure/<path>.nbt.
    private static final String STRUCTURE_DIRECTORY = "structure";

    private final PackOutput.PathProvider pathProvider;

    public GameTestStructureProvider(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, STRUCTURE_DIRECTORY);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(
                saveStructure(output, ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "loadsemptytemplate"), 3, 3, 3),
                saveStructure(output, ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "reactor_space"), 9, 9, 9));
    }

    // Builds an all-air structure template of the given size and writes it as a gzip-compressed .nbt (the binary format
    // StructureTemplateManager loads) through the cached output.
    private CompletableFuture<?> saveStructure(CachedOutput output, ResourceLocation id, int sizeX, int sizeY, int sizeZ) {
        CompoundTag tag = emptyTemplate(sizeX, sizeY, sizeZ);
        Path path = this.pathProvider.file(id, "nbt");
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HashingOutputStream hashing = new HashingOutputStream(Hashing.sha1(), bytes);
                writeCompressed(tag, hashing);
                output.writeIfNeeded(path, bytes.toByteArray(), hashing.hash());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save gametest structure " + id, e);
            }
        });
    }

    private static void writeCompressed(CompoundTag tag, OutputStream out) throws IOException {
        NbtIo.writeCompressed(tag, out);
    }

    // An empty StructureTemplate NBT of the given size: a one-entry air palette, two air blocks at opposite corners to
    // pin the bounds, no entities, stamped with the current data version. Mirrors the shape StructureTemplate#save
    // writes, so StructureTemplate#load reads it back without complaint.
    private static CompoundTag emptyTemplate(int sizeX, int sizeY, int sizeZ) {
        CompoundTag tag = new CompoundTag();

        ListTag palette = new ListTag();
        palette.add(NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
        tag.put("palette", palette);

        ListTag blocks = new ListTag();
        blocks.add(airBlock(0, 0, 0));
        blocks.add(airBlock(sizeX - 1, sizeY - 1, sizeZ - 1));
        tag.put("blocks", blocks);

        tag.put("entities", new ListTag());
        tag.put("size", intList(sizeX, sizeY, sizeZ));

        return NbtUtils.addCurrentDataVersion(tag);
    }

    // A {pos:[x,y,z], state:0} block entry referencing the single (air) palette index.
    private static CompoundTag airBlock(int x, int y, int z) {
        CompoundTag block = new CompoundTag();
        block.put("pos", intList(x, y, z));
        block.putInt("state", 0);
        return block;
    }

    private static ListTag intList(int... values) {
        ListTag list = new ListTag();
        List<IntTag> ints = new ArrayList<>(values.length);
        for (int value : values) {
            ints.add(IntTag.valueOf(value));
        }
        list.addAll(ints);
        return list;
    }

    @Override
    public String getName() {
        return "Alchemistry GameTest Structures";
    }
}
