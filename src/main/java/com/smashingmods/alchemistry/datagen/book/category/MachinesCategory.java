package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.AtomizerEntry;
import com.smashingmods.alchemistry.datagen.book.entry.CombinerEntry;
import com.smashingmods.alchemistry.datagen.book.entry.CompactorEntry;
import com.smashingmods.alchemistry.datagen.book.entry.DissolverEntry;
import com.smashingmods.alchemistry.datagen.book.entry.LiquifierEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class MachinesCategory extends CategoryProvider {

    public static final String ID = "machines_category";

    public MachinesCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // The five processing machines are independent topics, so they sit as floating nodes (no parent
        // links) laid out in a compact, non-overlapping grid: combiner and dissolver on top, the three
        // supporting machines below.
        return new String[]{
                "_b_d_",
                "a_c_l"
        };
    }

    @Override
    protected void generateEntries() {
        add(new CombinerEntry(this).generate('b'));
        add(new DissolverEntry(this).generate('d'));
        add(new AtomizerEntry(this).generate('a'));
        add(new CompactorEntry(this).generate('c'));
        add(new LiquifierEntry(this).generate('l'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(1);
    }

    @Override
    protected String categoryName() {
        return "Machines";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\\\nEvery great laboratory starts with the right equipment. These powered machines break materials down into their chemical elements and compounds and put them back together again -- the foundation of everything else in Alchemistry.";
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
