package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class LiquifierEntry extends EntryProvider {

    public static final String ID = "liquifier";

    public LiquifierEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The Liquifier is a powered machine that can be used convert elements and compounds into their fluid equivalents.\n\n*From Professor Warren's notes: Of course, every process must be reversible. The right amount of pressure can be used to force the atomic phials back into liquid form by compression against iron.*");

        page("crafting", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:liquifier"));
    }

    @Override
    protected String entryName() {
        return "Element Liquifier";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.LIQUIFIER.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
