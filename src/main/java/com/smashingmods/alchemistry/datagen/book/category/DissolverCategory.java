package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.DissolverEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class DissolverCategory extends CategoryProvider {

    public static final String ID = "dissolver_category";

    public DissolverCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{"d"};
    }

    @Override
    protected void generateEntries() {
        add(new DissolverEntry(this).generate('d'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(3);
    }

    @Override
    protected String categoryName() {
        return "Chemical Dissolver";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\nWe've all been there. You come back from a mining expedition and have a ton of useless cobblestone, or your survivalist farm of beetroot is now just taking up room. Have no fear! By throwing these items into our Chemical Dissolver, you can extract useful elements from them! Turn those beets into iron! Find gold in that cobblestone, and sometimes even rarer things!";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(BlockRegistry.DISSOLVER.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
