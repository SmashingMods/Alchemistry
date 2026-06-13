package com.smashingmods.alchemistry.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static com.smashingmods.alchemistry.Alchemistry.MODID;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-0 pin for the block-item description ids. Since 1.21.2 a {@code BlockItem} whose {@link Item.Properties}
 * lack {@code useBlockDescriptionPrefix} bakes an {@code item.<ns>.<path>} description id, but the lang datagen
 * only emits {@code block.alchemistry.*} keys, so every machine/reactor item showed a raw {@code item.alchemistry.*}
 * key. This asserts the {@link Item.Properties} the {@link ItemRegistry#fromBlock} path registers with
 * ({@link ItemRegistry#blockItemProperties()}) bake a {@code block.}-prefixed id for the compactor, the other
 * machines and the reactor parts.
 *
 * <p>The description id is resolved from {@link Item.Properties} alone -- the prefix plus the registered id -- so no
 * {@code Item} (or block) is constructed: doing so after {@code Bootstrap} hits a frozen-registry intrusive-holder
 * write. Instead the id is set on the production properties (as {@code DeferredRegister} does) and the package-private
 * {@code effectiveDescriptionId()} -- the exact method {@code Item}'s constructor reads -- is invoked reflectively.
 * {@code Util.makeDescriptionId} is pure, so this needs neither a bootstrap nor a loaded language; it pins the key's
 * prefix, not its translation.</p>
 */
class BlockItemDescriptionIdTest {

    // The compactor (the reported missing-texture/raw-key regression), the other four machines, both chamber
    // controllers and the reactor I/O parts -- every block ItemRegistry.fromBlock registers an item for.
    private static final List<String> BLOCK_ITEM_PATHS = List.of(
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
    void blockItems_useBlockDescriptionPrefix() {
        for (String path : BLOCK_ITEM_PATHS) {
            String descriptionId = descriptionIdFor(path);
            assertTrue(descriptionId.startsWith("block."),
                    () -> "block-item " + path + " must use the block. description prefix, got " + descriptionId);
        }
    }

    // Resolves the description id the production block-item properties bake for the given path, mirroring the
    // DeferredRegister registration: take ItemRegistry.blockItemProperties(), set the item id, and read back the
    // effectiveDescriptionId() the Item constructor would use -- without constructing an Item.
    private static String descriptionIdFor(String path) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, path));
        Item.Properties properties = ItemRegistry.blockItemProperties().setId(itemKey);
        try {
            Method effectiveDescriptionId = Item.Properties.class.getDeclaredMethod("effectiveDescriptionId");
            effectiveDescriptionId.setAccessible(true);
            return (String) effectiveDescriptionId.invoke(properties);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to resolve the effective description id for " + path, exception);
        }
    }
}
