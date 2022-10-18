package com.smashingmods.alchemistry.api.blockentity.container;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemistry.api.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeDisplayUtil {

    public static List<Component> getItemTooltipComponent(ItemStack pItemStack, BaseComponent pComponent) {
        List<Component> components = new ArrayList<>();
        Objects.requireNonNull(pItemStack.getItem().getRegistryName());
        String namespace = StringUtils.capitalize(pItemStack.getItem().getRegistryName().getNamespace());

        components.add(pComponent.withStyle(ChatFormatting.UNDERLINE, ChatFormatting.YELLOW));
        components.add(new TextComponent(String.format("%dx %s", pItemStack.getCount(), pItemStack.getItem().getDescription().getString())));

        if (pItemStack.getItem() instanceof Chemical chemical) {

            String abbreviation = chemical.getAbbreviation();

            if (chemical instanceof ElementItem element) {
                components.add(new TextComponent(String.format("%s (%d)", abbreviation, element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
                components.add(new TextComponent(element.getGroupName()).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof ChemicalItem chemicalItem && !chemicalItem.getItemType().equals(ChemicalItemType.COMPOUND)) {
                ElementItem element = (ElementItem) chemicalItem.getChemical();
                components.add(new TextComponent(String.format("%s (%d)", chemicalItem.getAbbreviation(), element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
                components.add(new TextComponent(element.getGroupName()).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof CompoundItem) {
                components.add(new TextComponent(abbreviation).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
        components.add(new TextComponent(namespace).withStyle(ChatFormatting.BLUE));
        return components;
    }

    public static Pair<ResourceLocation, String> getSearchablePair(ProcessingRecipe pRecipe) {

        if (pRecipe instanceof AtomizerRecipe atomizerRecipe) {

            Objects.requireNonNull(atomizerRecipe.getInput().getFluid().getRegistryName());
            ResourceLocation left = atomizerRecipe.getInput().getFluid().getRegistryName();
            String right = atomizerRecipe.getInput().getDisplayName().getString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof CombinerRecipe combinerRecipe) {

            Objects.requireNonNull(combinerRecipe.getOutput().getItem().getRegistryName());
            ResourceLocation left = combinerRecipe.getOutput().getItem().getRegistryName();
            String right = combinerRecipe.getOutput().getItem().getDescription().toString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof CompactorRecipe compactorRecipe) {

            Objects.requireNonNull(compactorRecipe.getOutput().getItem().getRegistryName());
            ResourceLocation left = compactorRecipe.getOutput().getItem().getRegistryName();
            String right = compactorRecipe.getOutput().getItem().getDescription().toString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof DissolverRecipe dissolverRecipe) {

            Objects.requireNonNull(dissolverRecipe.getInput().getRegistryName());
            ResourceLocation left = dissolverRecipe.getInput().getRegistryName();
            String right = dissolverRecipe.getInput().getRegistryName().getPath().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof FissionRecipe fissionRecipe) {

            Objects.requireNonNull(fissionRecipe.getInput().getItem().getRegistryName());
            ResourceLocation left = fissionRecipe.getInput().getItem().getRegistryName();
            String right = fissionRecipe.getInput().getItem().getDescription().toString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof FusionRecipe fusionRecipe) {

            Objects.requireNonNull(fusionRecipe.getOutput().getItem().getRegistryName());
            ResourceLocation left = fusionRecipe.getOutput().getItem().getRegistryName();
            String right = fusionRecipe.getOutput().getItem().getDescription().toString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof LiquifierRecipe liquifierRecipe) {

            Objects.requireNonNull(liquifierRecipe.getInput().getRegistryName());
            ResourceLocation left = liquifierRecipe.getOutput().getFluid().getRegistryName();
            String right = liquifierRecipe.getOutput().getDisplayName().toString().toLowerCase();
            return Pair.of(left, right);

        }
        return Pair.of(new ResourceLocation("minecraft:empty"), "");
    }

    public static ItemStack getTarget(ProcessingRecipe pRecipe) {

        if (pRecipe instanceof AtomizerRecipe atomizerRecipe) {
            return atomizerRecipe.getOutput();
        } else if (pRecipe instanceof CombinerRecipe combinerRecipe) {
            return combinerRecipe.getOutput();
        } else if (pRecipe instanceof CompactorRecipe compactorRecipe) {
            return compactorRecipe.getOutput();
        } else if (pRecipe instanceof DissolverRecipe dissolverRecipe) {
            return !dissolverRecipe.getInput().toStacks().isEmpty() ? dissolverRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
        } else if (pRecipe instanceof FissionRecipe fissionRecipe) {
            return fissionRecipe.getInput();
        } else if (pRecipe instanceof FusionRecipe fusionRecipe) {
            return fusionRecipe.getOutput();
        } else if (pRecipe instanceof LiquifierRecipe liquifierRecipe) {
            return liquifierRecipe.getInput().toStacks().isEmpty() ? liquifierRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getRecipeInputByIndex(ProcessingRecipe pRecipe, int pIndex) {
        AtomicReference<ItemStack> toReturn = new AtomicReference<>(ItemStack.EMPTY);
        if (pRecipe instanceof AtomizerRecipe atomizerRecipe) {
            return atomizerRecipe.getOutput();
        } else if (pRecipe instanceof CombinerRecipe combinerRecipe) {
            if (pIndex >= 0 && pIndex < combinerRecipe.getInput().size()) {
                new Random().ints(0, combinerRecipe.getInput().get(pIndex).toStacks().size())
                        .findFirst()
                        .ifPresent(random -> toReturn.set(combinerRecipe.getInput().get(pIndex).toStacks().get(random)));
            }
        } else if (pRecipe instanceof CompactorRecipe compactorRecipe) {
            new Random().ints(0, compactorRecipe.getInput().toStacks().size())
                    .findFirst()
                    .ifPresent(random -> toReturn.set(compactorRecipe.getInput().toStacks().get(random)));
        } else if (pRecipe instanceof DissolverRecipe dissolverRecipe) {
            return !dissolverRecipe.getInput().toStacks().isEmpty() ? dissolverRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
        } else if (pRecipe instanceof FissionRecipe fissionRecipe) {
            return fissionRecipe.getInput();
        } else if (pRecipe instanceof FusionRecipe fusionRecipe) {
            return fusionRecipe.getOutput();
        } else if (pRecipe instanceof LiquifierRecipe liquifierRecipe) {
            return liquifierRecipe.getInput().toStacks().isEmpty() ? liquifierRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
        }
        return toReturn.get();
    }

    public static int getInputSize(AbstractProcessingBlockEntity pBlockEntity) {

        if (pBlockEntity instanceof AtomizerBlockEntity) {
            return 1;
        } else if (pBlockEntity instanceof CombinerBlockEntity) {
            return 4;
        } else if (pBlockEntity instanceof CompactorBlockEntity) {
            return 1;
        } else if (pBlockEntity instanceof DissolverBlockEntity) {
            return 1;
        } else if (pBlockEntity instanceof FissionControllerBlockEntity) {
            return 1;
        } else if (pBlockEntity instanceof FusionControllerBlockEntity) {
            return 2;
        } else if (pBlockEntity instanceof LiquifierBlockEntity) {
            return 1;
        }
        return 0;
    }
}
