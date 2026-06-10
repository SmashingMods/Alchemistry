package com.smashingmods.alchemistry.datagen.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.smashingmods.alchemistry.datagen.book.entry.FissionEntry;
import com.smashingmods.alchemistry.datagen.book.entry.FissionMultiblockEntry;
import com.smashingmods.alchemistry.datagen.book.entry.FusionEntry;
import com.smashingmods.alchemistry.datagen.book.entry.FusionMultiblockEntry;
import com.smashingmods.alchemistry.datagen.book.entry.ReactorEntry;
import com.smashingmods.alchemistry.registry.BlockRegistry;

public class ReactorCategory extends CategoryProvider {

    public static final String ID = "reactor_category";

    public ReactorCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // Reactor overview is the root at the top; fission branches left, fusion right, each with its
        // multiblock entry directly below it.
        return new String[]{
                "___r___",
                "_f___u_",
                "_F___U_"
        };
    }

    @Override
    protected void generateEntries() {
        BookEntryModel reactor = add(new ReactorEntry(this).generate('r'));

        BookEntryModel fission = add(new FissionEntry(this).generate('f'))
                .withParent(parent(reactor));
        add(new FissionMultiblockEntry(this).generate('F'))
                .withParent(parent(fission));

        BookEntryModel fusion = add(new FusionEntry(this).generate('u'))
                .withParent(parent(reactor));
        add(new FusionMultiblockEntry(this).generate('U'))
                .withParent(parent(fusion));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withSortNumber(7);
    }

    @Override
    protected String categoryName() {
        return "Reactor Overview";
    }

    @Override
    protected String categoryDescription() {
        return "An excerpt from the Alchemistry Labs Catalog:\\\nSplitting the atom?  Bring it on!  Now you can turn oxygen into beryllium, and you know what that's good for?  Emeralds!  Beat those mumbling Villagers at their own game!\\\n\\\nHarness the intense heat and pressure of a Star!  Here at Alchemistry Labs, we've stol- er- FOUND a way to achieve it much more simply.  Now you can turn all that extra nitrogen and oxygen into phosphorus.  Glowstone without going near the Nether!  Well, except for that initial construction...";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(BlockRegistry.REACTOR_CASING.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
