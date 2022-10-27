package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet.Builder.createSet;
import static com.smashingmods.alchemistry.datagen.DatagenUtil.tagNotEmptyCondition;

public class ChemlibRecipes extends DissolverRecipeProvider {

    public ChemlibRecipes(Consumer<FinishedRecipe> pConsumer) {
        super(pConsumer);
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new ChemlibRecipes(pConsumer).register();
    }

    protected void register() {
        ItemRegistry.getCompounds().forEach(compoundItem -> {
            List<ItemStack> components = new ArrayList<>();
            compoundItem.getComponents().forEach((name, count) -> {
                Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(name);
                Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(name);
                optionalElement.ifPresent(element -> components.add(new ItemStack(element, count)));
                optionalCompound.ifPresent(compound -> components.add(new ItemStack(compound, count)));
            });
            dissolver(compoundItem, createSet().addGroup(components, 100).build(), true);
        });

        for (CompoundItem compound : ItemRegistry.getCompounds()) {
            if (compound.getMatterState().equals(MatterState.SOLID)) {
                List<ItemStack> components = new ArrayList<>();
                Optional<ChemicalItem> input = ItemRegistry.getChemicalItemByNameAndType(compound.getChemicalName(), ChemicalItemType.COMPOUND);
                components.add(new ItemStack(compound, 8));
                input.ifPresent(chemicalItem -> dissolver(chemicalItem, createSet().addGroup(components).build(), true));
            }
        }

        for (ElementItem element : ItemRegistry.getElementsByMatterState(MatterState.SOLID).toList()) {
            String ingotTag = String.format("forge:ingots/%s", element.getChemicalName());
            String nuggetTag = "forge:nuggets/" + element.getChemicalName();
            String dustTag = "forge:dusts/" + element.getChemicalName();
            String plateTag = "forge:plates/" + element.getChemicalName();
            String oreTag = "forge:ores/" + element.getChemicalName();
            String storageBlockTag = "forge:storage_blocks/" + element.getChemicalName();

            if (!element.getChemicalName().equals("sulfur")) {
                dissolver(ingotTag, createSet().addGroup(new ItemStack(element, 16)).build(), tagNotEmptyCondition(ingotTag));
                dissolver(nuggetTag, createSet().addGroup(new ItemStack(element, 1)).build(), tagNotEmptyCondition(nuggetTag));
                dissolver(dustTag, createSet().addGroup(new ItemStack(element, 16)).build(), tagNotEmptyCondition(dustTag));
                dissolver(oreTag, createSet().addGroup(new ItemStack(element, 32)).build(), tagNotEmptyCondition(oreTag));
                dissolver(storageBlockTag, createSet().addGroup(new ItemStack(element, 144)).build(), tagNotEmptyCondition(storageBlockTag));
            }

            dissolver(plateTag, createSet().addGroup(new ItemStack(element, 16)).build(), tagNotEmptyCondition(plateTag));
        }
    }
}
