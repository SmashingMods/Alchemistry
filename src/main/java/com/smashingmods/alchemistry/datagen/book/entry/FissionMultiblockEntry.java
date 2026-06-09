package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class FissionMultiblockEntry extends EntryProvider {

    public static final String ID = "fission_multiblock";

    public FissionMultiblockEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockName("Fission Multiblock")
                .withMultiblockId(modLoc("fission_reactor")));

        page("required", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
        pageTitle("Required Blocks");
        pageText("1x Fission Chamber Controller\\3x Fission Core\\1x Reactor Energy Input\\1x Reactor Item Input\\1x Reactor Item Output\\62x Reactor Casing\\36x Reactor Glass");

        page("optional", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
        pageTitle("Optional Blocks");
        pageText("Reactor energy input and item input/output blocks can be placed anywhere that requires a reactor casing but not anywhere there is reactor glass.\\\\Reactor glass is optional. Anywhere that requires reactor glass can be replaced with reactor casing. Make it look however you want.");
    }

    @Override
    protected String entryName() {
        return "Fission Multiblock";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(BlockRegistry.REACTOR_CASING.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
