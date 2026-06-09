package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class FissionEntry extends EntryProvider {

    public static final String ID = "fission";

    public FissionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The Fission Reactor allows you to split apart the very fabric that holds elements together. All elements except for Hydrogen can be split with the fission multiblock. See JEI for recipes.\\\\*From Professor Warren's notes: The rods dropped from the volatile blazes have inspired me to make a new machine.*\\\\The Fission Reactor is a 5x5x5 cubic structure, featuring a ceiling, floor and corner pillars of reactor casings. There are three fission core blocks in the center. The walls are filled in reactor casing or reactor glass. The chamber is entirely inert however without the fission chamber controller, energy input, and item input/output blocks.");

        page("controller", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:fission_chamber_controller").withText(context().pageText()));
        pageText("While not the core, the chamber controller is the heart of the fission reactor. This machine controls everything that happens in the core. It takes power and can have an item input and output.");

        page("core", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:fission_core").withText(context().pageText()));
        pageText("The core and the surrounding air around it are where fission takes place. Items are placed inside the core and then the atom is split which leaves behind elements to use in your experiments.");
    }

    @Override
    protected String entryName() {
        return "Fission Overview";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.FISSION_CONTROLLER.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
