package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class ReactorEntry extends EntryProvider {

    public static final String ID = "reactor";

    public ReactorEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The fission and fusion reactors are an integral part of manipulating matter to form elements for whatever you need to create. They are complex multiblock machines with a lot of requirements to get started.\n\nEvery reactor will need a number of these blocks:\n- Reactor Casing\n- Reactor Glass\n- Reactor Controller\n- Reactor Core\n- Energy Input\n- Item Input\n- Item Output");

        page("casing", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:reactor_casing")
                .withTitle1(context().pageTitle()).withText(context().pageText()));
        pageTitle("Reactor Casing");
        pageText("Reactor Casing makes up the majority of the reactor's structure. The casing has the strength to hold up against the pressure of splitting and merging elements. You will need a lot of these to make a single reactor.");

        page("glass", () -> BookSpotlightPageModel.create().withTitle(context().pageTitle()).withText(context().pageText())
                .withItem(BlockRegistry.REACTOR_GLASS.get()));
        pageTitle("Reactor Glass");
        pageText("Reactor glass allows you to view inside of your reactor. It's lead lined, which will hopefully keep the radiation at bay -- maybe. These are made in the combiner, check JEI for the recipe.");

        page("energy", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:reactor_energy")
                .withTitle1(context().pageTitle()).withText(context().pageText()));
        pageTitle("Reactor Energy");
        pageText("Your reactors need power -- a **lot** of power. This block can be connected to energy cables that are hopefully up to the challenge.");

        page("input", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:reactor_input")
                .withTitle1(context().pageTitle()).withText(context().pageText()));
        pageTitle("Reactor Input");
        pageText("You can place your elements into the reactor by hand, but what's the fun in that? Automate your reactors by hooking them up to your item pipes.");

        page("output", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:reactor_output")
                .withTitle1(context().pageTitle()).withText(context().pageText()));
        pageTitle("Reactor Output");
        pageText("Basically the same as the reactor input but in reverse.");
    }

    @Override
    protected String entryName() {
        return "Reactors: An Overview";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.REACTOR_CASING.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
