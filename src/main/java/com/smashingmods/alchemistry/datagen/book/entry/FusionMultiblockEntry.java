package com.smashingmods.alchemistry.datagen.book.entry;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;

public class FusionMultiblockEntry extends EntryProvider {

    public static final String ID = "fusion_multiblock";

    public FusionMultiblockEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockName("Fusion Multiblock")
                .withMultiblockId(modLoc("fusion_reactor")));

        page("required", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
        pageTitle("Required Blocks");
        pageText("1x Fusion Chamber Controller\\\n3x Fusion Core\\\n1x Reactor Energy Input\\\n1x Reactor Item Input\\\n1x Reactor Item Output\\\n62x Reactor Casing\\\n36x Reactor Glass");

        page("optional", () -> BookTextPageModel.create().withTitle(context().pageTitle()).withText(context().pageText()));
        pageTitle("Optional Blocks");
        pageText("Reactor energy input and item input/output blocks can be placed anywhere that requires a reactor casing but not anywhere there is reactor glass.\\\n\\\nReactor glass is optional. Anywhere that requires reactor glass can be replaced with reactor casing. Make it look however you want.");
    }

    @Override
    protected String entryName() {
        return "Fusion Multiblock";
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
