package com.smashingmods.alchemistry.client.container;

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
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.recipe.ProcessingRecipe;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeDisplayUtil {

    public static List<Component> getItemTooltipComponent(ItemStack pItemStack, MutableComponent pComponent) {
        List<Component> components = new ArrayList<>();
        String namespace = StringUtils.capitalize(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItemStack.getItem())).getNamespace());

        components.add(pComponent.withStyle(ChatFormatting.UNDERLINE, ChatFormatting.YELLOW));
        components.add(MutableComponent.create(PlainTextContents.create(String.format("%dx %s", pItemStack.getCount(), pItemStack.getItem().getName().getString()))));

        if (pItemStack.getItem() instanceof Chemical chemical) {

            String abbreviation = chemical.getAbbreviation();

            if (chemical instanceof ElementItem element) {
                components.add(MutableComponent.create(PlainTextContents.create(String.format("%s (%d)", abbreviation, element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
                components.add(MutableComponent.create(PlainTextContents.create(element.getGroupName())).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof ChemicalItem chemicalItem && !chemicalItem.getItemType().equals(ChemicalItemType.COMPOUND)) {
                if (chemicalItem.getChemical() instanceof ElementItem element) {
                    components.add(MutableComponent.create(PlainTextContents.create(String.format("%s (%d)", chemicalItem.getAbbreviation(), element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
                    components.add(MutableComponent.create(PlainTextContents.create(element.getGroupName())).withStyle(ChatFormatting.GRAY));
                }
                components.add(MutableComponent.create(PlainTextContents.create(chemicalItem.getAbbreviation())).withStyle(ChatFormatting.DARK_AQUA));
            } else if (chemical instanceof CompoundItem) {
                components.add(MutableComponent.create(PlainTextContents.create(abbreviation)).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
        components.add(MutableComponent.create(PlainTextContents.create((namespace))).withStyle(ChatFormatting.BLUE));
        return components;
    }

    public static Pair<ResourceLocation, String> getSearchablePair(ProcessingRecipe pRecipe) {

        if (pRecipe instanceof AtomizerRecipe atomizerRecipe) {

            ResourceLocation left = BuiltInRegistries.FLUID.getKey(atomizerRecipe.getInput().getFluid());
            String right = atomizerRecipe.getInput().getHoverName().getString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof CombinerRecipe combinerRecipe) {

            ResourceLocation left = BuiltInRegistries.ITEM.getKey(combinerRecipe.getOutput().getItem());
            String right = combinerRecipe.getOutput().getItem().getName().getString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof CompactorRecipe compactorRecipe) {

            ResourceLocation left = BuiltInRegistries.ITEM.getKey(compactorRecipe.getOutput().getItem());
            String right = compactorRecipe.getOutput().getItem().getName().getString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof DissolverRecipe dissolverRecipe) {

            ResourceLocation left = dissolverRecipe.getInput().getRegistryName();
            String right = dissolverRecipe.getInput().getRegistryName().getPath().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof FissionRecipe fissionRecipe) {

            ResourceLocation left = BuiltInRegistries.ITEM.getKey(fissionRecipe.getInput().getItem());
            String right = fissionRecipe.getInput().getItem().getName().getString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof FusionRecipe fusionRecipe) {

            ResourceLocation left = BuiltInRegistries.ITEM.getKey(fusionRecipe.getOutput().getItem());
            String right = fusionRecipe.getOutput().getItem().getName().getString().toLowerCase();
            return Pair.of(left, right);

        } else if (pRecipe instanceof LiquifierRecipe liquifierRecipe) {

            ResourceLocation left = BuiltInRegistries.FLUID.getKey(liquifierRecipe.getOutput().getFluid());
            String right = liquifierRecipe.getOutput().getHoverName().getString().toLowerCase();
            return Pair.of(left, right);

        }
        return Pair.of(ResourceLocation.parse("minecraft:empty"), "");
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
            return !liquifierRecipe.getInput().toStacks().isEmpty() ? liquifierRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getRecipeInputByIndex(ProcessingRecipe pRecipe, int pIndex) {
        AtomicReference<ItemStack> toReturn = new AtomicReference<>(ItemStack.EMPTY);
        if (pRecipe instanceof AtomizerRecipe atomizerRecipe) {
            return atomizerRecipe.getOutput();
        } else if (pRecipe instanceof CombinerRecipe combinerRecipe) {
            if (pIndex >= 0 && pIndex < combinerRecipe.getInput().size()) {
                List<ItemStack> stacks = combinerRecipe.getInput().get(pIndex).toStacks();
                // An ingredient can resolve to zero stacks (e.g. an empty tag client-side); Random.ints(0, 0) throws,
                // so only pick when there is something to pick and otherwise leave the EMPTY default in place.
                if (!stacks.isEmpty()) {
                    new Random().ints(0, stacks.size())
                            .findFirst()
                            .ifPresent(random -> toReturn.set(stacks.get(random)));
                }
            }
        } else if (pRecipe instanceof CompactorRecipe compactorRecipe) {
            List<ItemStack> stacks = compactorRecipe.getInput().toStacks();
            if (!stacks.isEmpty()) {
                new Random().ints(0, stacks.size())
                        .findFirst()
                        .ifPresent(random -> toReturn.set(stacks.get(random)));
            }
        } else if (pRecipe instanceof DissolverRecipe dissolverRecipe) {
            return !dissolverRecipe.getInput().toStacks().isEmpty() ? dissolverRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
        } else if (pRecipe instanceof FissionRecipe fissionRecipe) {
            return fissionRecipe.getInput();
        } else if (pRecipe instanceof FusionRecipe fusionRecipe) {
            return fusionRecipe.getOutput();
        } else if (pRecipe instanceof LiquifierRecipe liquifierRecipe) {
            return !liquifierRecipe.getInput().toStacks().isEmpty() ? liquifierRecipe.getInput().toStacks().get(0) : ItemStack.EMPTY;
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
