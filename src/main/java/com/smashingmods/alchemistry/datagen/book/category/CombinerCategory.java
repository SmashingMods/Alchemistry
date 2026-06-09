package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.CombinerEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class CombinerCategory extends CategoryProvider {

    public static final String ID = "combiner_category";

    public CombinerCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{"c"};
    }

    @Override
    protected void generateEntries() {
        add(new CombinerEntry(this).generate('c'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(2);
    }

    @Override
    protected String categoryName() {
        return "Chemical Combiner";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\nCompounds and elements are nice, but they're so boring. They sit around and don't do anything. With our Combiner, you can rearrange the products of the Dissolver into much more exciting things! Got potatoes? You've got everything you need for Potassium Nitrate! Add a little carbon and sulfur, and KABOOM! Carrots have a lot of carbon for this - or even for diamonds! Carrots to carats!";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(BlockRegistry.COMBINER.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
