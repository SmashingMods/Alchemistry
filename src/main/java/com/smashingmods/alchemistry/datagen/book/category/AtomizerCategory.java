package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.AtomizerEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class AtomizerCategory extends CategoryProvider {

    public static final String ID = "atomizer_category";

    public AtomizerCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{"a"};
    }

    @Override
    protected void generateEntries() {
        add(new AtomizerEntry(this).generate('a'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(5);
    }

    @Override
    protected String categoryName() {
        return "Atomizer";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\nTired of having to mop up laboratory spills?  It's a mess, and never mind the biohazard problems!  Our Atomizer will turn that bucket of water into convenient phials of H₂O!";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(BlockRegistry.ATOMIZER.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
