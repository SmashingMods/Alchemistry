package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class DissolverEntry extends EntryProvider {

    public static final String ID = "dissolver";

    public DissolverEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("intro", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("The Chemical Dissolver is a powered machine, and it, along with the Chemical Combiner are the most essential machines in Alchemistry.\\\nThe Chemical Dissolver takes inputs and breaks them down into different chemical elements and compounds.\\\n\\\n*From Professor Warren's notes: I've been experimenting with the properties of magma, as a less volatile form of Lava. While Lava will destroy most things, it turns out that I can use magma to more precisely break down materials into their components. Some components are more firmly bonded, and require an additional pass or two to be completely broken down.*");

        page("relative", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("There are two types of Chemical Dissolver recipes which are denoted in each JEI recipe. First, there are recipes with [#](55ffff)relative probability[#]() where all possible output groups add up to 100% and one output group will always be chosen for each [#](55ffff)roll[#](). Some recipes may have empty groups, however, resulting in a chance of no output.");

        page("absolute", () -> BookTextPageModel.create().withText(context().pageText()));
        pageText("There are also recipes with [#](55ffff)absolute probability[#](). With absolute probability each group is rolled independently with its own chance of being outputted ignoring the results of other rolls.");

        page("crafting", () -> BookCraftingRecipePageModel.create().withRecipeId1("alchemistry:dissolver"));
    }

    @Override
    protected String entryName() {
        return "Chemical Dissolver";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.DISSOLVER.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
