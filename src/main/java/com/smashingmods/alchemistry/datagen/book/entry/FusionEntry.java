package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class FusionEntry extends EntryProvider {

    public static final String ID = "fusion";

    public FusionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The Fusion Reactor allows you to fuse two elements together to create new elements. The output will be decided by the sum of the atomic number of the two inputs. See JEI for recipes.\n\n*From Professor Warren's notes: Fusion requires the use of a star, and some elements only found in the depths of the Nether. Using these to make a few changes to the Fission Reactor components gives me a more powerful reactor, capable of fusing two elements into one with the sum of their atomic numbers. With careful applications of fission and fusion, I can now turn literally any element into any other one.*");

        page("controller", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:fusion_chamber_controller").withText(context().pageText()));
        pageText("The fusion chamber core controls the process of smashing elements together. It takes power and can have an item input and output.");

        page("core", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:fusion_core").withText(context().pageText()));
        pageText("The place where two becomes one; in the reactor core and surrounding air. Items are placed inside the core and slammed together at high speeds to merge smaller elements into bigger ones for your other experiments.");
    }

    @Override
    protected String entryName() {
        return "Fusion Overview";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.FUSION_CONTROLLER.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
