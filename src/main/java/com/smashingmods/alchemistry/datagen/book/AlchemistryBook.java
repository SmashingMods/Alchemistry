package com.smashingmods.alchemistry.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.smashingmods.alchemistry.datagen.book.category.MachinesCategory;
import com.smashingmods.alchemistry.datagen.book.category.ReactorCategory;
import net.minecraft.resources.ResourceLocation;

/**
 * The Alchemistry guidebook ("Alchemistry Labs Catalogue"), authored through Modonomicon's datagen API. The
 * book id stays {@code alchemistry_book} so the book-grant recipe's {@code modonomicon:book_id} component keeps
 * referencing {@code alchemistry:alchemistry_book}. Every name/description/title/text is added as a translation
 * key into the shared language cache passed in; the lang values are written out by {@code LocalizationGenerator}.
 */
public class AlchemistryBook extends SingleBookSubProvider {

    public static final String ID = "alchemistry_book";

    public AlchemistryBook(String modId, ModonomiconLanguageProvider lang) {
        super(ID, modId, lang);
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        // modonomicon_blue is the book item model closest to Patchouli's former book_blue look.
        return book.withModel(ResourceLocation.parse("modonomicon:modonomicon_blue"))
                .withCreativeTab(modLoc("machine_tab"));
    }

    @Override
    protected void registerDefaultMacros() {
        // No custom macros: page text is authored directly in Modonomicon markdown.
    }

    @Override
    protected void generateCategories() {
        add(new MachinesCategory(this).generate());
        add(new ReactorCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "Alchemistry Labs Catalogue";
    }

    @Override
    protected String bookTooltip() {
        return "Looking to smash some atoms together? This catalogue will outline the machines you can manufacture in your progression through Alchemistry.";
    }
}
