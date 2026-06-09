package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.CompactorEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class CompactorCategory extends CategoryProvider {

    public static final String ID = "compactor_category";

    public CompactorCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{"c"};
    }

    @Override
    protected void generateEntries() {
        add(new CompactorEntry(this).generate('c'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(4);
    }

    @Override
    protected String categoryName() {
        return "Chemical Compactor";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\nSo, you've broken down materials with the Chemical Dissolver. The next thing to do is put them back together! No one can spend a test tube after all. The Compactor can squeeze the silcon dioxide from cobble and stone into more attractive granite, or that gold you found into easily smelted dust. Shiny!";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(BlockRegistry.COMPACTOR.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
