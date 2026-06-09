package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class CombinerEntry extends EntryProvider {

    public static final String ID = "combiner";

    public CombinerEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The Chemical Combiner is a powered machine, perhaps the most important machine of all in Alchemistry. It allows you to combine elements, compounds, and occasionally other items to form new items. The combiner takes up to 4 input items similar to a 2x2 crafting grid, but unlike a crafting grid it allows each input slot to contain more than one item per craft.\n\n*From Professor Warren's notes: The Compactor is all well and good, but rather simplistic.  I'm interested in what I can do with these components. With the focusing power of a diamond and the resistance of obsidian, I can use a piston to force these components into more complex items.*");

        page("crafting", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:combiner").withText(context().pageText()));
        pageText("While the combiner is very powerful, it's also easy to use. To get started, choose the item you want to craft in the combiner screen or use JEI to transfer the recipe.");
    }

    @Override
    protected String entryName() {
        return "Chemical Combiner";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.COMBINER.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
