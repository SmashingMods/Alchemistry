package com.smashingmods.alchemistry.datagen;

import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemylib.api.storage.SideMode;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Locale;
import java.util.Objects;

public class LocalizationGenerator extends LanguageProvider {

    // The Modonomicon guidebook collects its strings into this cache as the book is generated; we flush them
    // into en_us.json here so a single lang provider owns the file (Modonomicon's own provider would target the
    // same path). Populated before this provider runs because the data generator runs providers in order.
    private final LanguageProviderCache bookLang;

    public LocalizationGenerator(PackOutput pOutput, LanguageProviderCache bookLang) {
        super(pOutput, Alchemistry.MODID, "en_us");
        this.bookLang = bookLang;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void addTranslations() {
        BlockRegistry.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .map(BuiltInRegistries.BLOCK::getKey)
                .filter(Objects::nonNull)
                .map(ResourceLocation::getPath)
                .forEach(path -> add(String.format("block.alchemistry.%s", path), WordUtils.capitalize(path.replace("_", " "))));

        MenuRegistry.MENU_TYPES.getEntries().stream()
                .map(DeferredHolder::get)
                .map(BuiltInRegistries.MENU::getKey)
                .filter(Objects::nonNull)
                .map(ResourceLocation::getPath)
                .forEach(path -> {
                    path = path.replace("_menu", "");
                    String translation = WordUtils.capitalize(path.replace("_", " "));
                    add(String.format("alchemistry.container.%s", path), translation);
                    // weird place to put this, but gives all the stuff we want!
                    add(String.format("alchemistry.jei.%s", path), translation);
                });

        add("itemGroup.alchemistry", "Alchemistry");

        add("tooltip.alchemistry.energy_requirement", "Requires %d FE/t");
        add("tooltip.alchemistry.requires", "Requires");

        for (Direction direction : Direction.values()) {
            String key = "alchemistry.container.sides." + direction.getSerializedName();
            String value;
            if (direction == Direction.UP) {
                value = "Top";
            } else if (direction == Direction.DOWN) {
                value = "Bottom";
            } else {
                value = WordUtils.capitalize(direction.getName());
            }
            add(key, value);
        }
        add("alchemistry.container.sides.external", "External access");
        for (SideMode mode : SideMode.values()) {
            add("alchemistry.container.sides.mode." + mode.name().toLowerCase(Locale.ROOT), WordUtils.capitalize(mode.name().toLowerCase(Locale.ROOT)));
        }
        add("alchemistry.container.sides.current", "Currently: ");
        add("alchemistry.container.sides.title", "Configure Input/Output Sides");
        add("alchemistry.container.sides.button", "Input/Output Configuration");

        add("alchemistry.container.search", "Search...");
        add("alchemistry.container.select_recipe", "Select recipe:");
        add("alchemistry.container.current_recipe", "Current recipe:");
        add("alchemistry.container.required_input", "Required input item:");
        add("alchemistry.container.target", "Target");
        add("alchemistry.container.reset_target", "Reset Target");
        add("alchemistry.container.nothing", "Nothing");
        add("alchemistry.container.enable_autobalance", "Enable Auto-Balance");
        add("alchemistry.container.disable_autobalance", "Disable Auto-Balance");
        add("alchemistry.container.autoeject.title.enabled", "Auto-eject enabled");
        add("alchemistry.container.autoeject.title.disabled", "Auto-eject disabled");
        add("alchemistry.container.autoeject.tooltip.enabled", "Outputs are put in containers which are next to the output block");
        add("alchemistry.container.autoeject.tooltip.disabled", "Outputs are currently not inserted in nearby containers");

        add("alchemistry.jei.dissolver.relative", "Relative");
        add("alchemistry.jei.dissolver.absolute", "Absolute");
        add("alchemistry.jei.dissolver.type", "Type");
        add("alchemistry.jei.dissolver.rolls", "Rolls");
        add("alchemistry.jei.elements.description", "All elements (except Hydrogen) can be created with the Fusion Chamber multiblock.\\nThe multiblock accepts 2 elements as input and fuses them together to create a new element equal to the sum of their atomic numbers.\"");

        // Flush the Modonomicon guidebook's auto-collected keys (book/category/entry/page names and text).
        bookLang.data().forEach(this::add);
    }
}
