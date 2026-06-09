package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.LiquifierEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class LiquifierCategory extends CategoryProvider {

    public static final String ID = "liquifier_category";

    public LiquifierCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{"l"};
    }

    @Override
    protected void generateEntries() {
        add(new LiquifierEntry(this).generate('l'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(6);
    }

    @Override
    protected String categoryName() {
        return "Liquifier";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\nNaturally, you may want to turn that H₂O back into something you can use to water your garden. No fear! Our Liquifier does the trick! Just make sure that what you think is H₂O isn't H₂SO₄!";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(BlockRegistry.LIQUIFIER.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
