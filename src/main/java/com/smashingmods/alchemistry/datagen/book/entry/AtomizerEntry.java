package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class AtomizerEntry extends EntryProvider {

    public static final String ID = "atomizer";

    public AtomizerEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The Atomizer is a powered machine that is used to convert fluids into their equivalent Alchemistry elements or compounds.\n*From Professor Warren's notes: Liquids are the simplest things to break down. The mere use of pistons to force them out of cauldrons turns them into easily stored atomic phials.*");

        page("crafting", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:atomizer"));
    }

    @Override
    protected String entryName() {
        return "Fluid Atomizer";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.ATOMIZER.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
