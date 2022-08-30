package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.item.IngredientStack;
import com.smashingmods.alchemistry.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CombinerBlockEntity extends AbstractInventoryBlockEntity {

    private final int maxProgress = Config.Common.combinerTicksPerOperation.get();
    private List<CombinerRecipe> recipes = new ArrayList<>();
    private CombinerRecipe currentRecipe;
    private int selectedRecipeIndex = -1;
    private String editBoxText = "";

    public CombinerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getInputHandler().isEmpty() && !isRecipeLocked()) {
            RecipeRegistry.getCombinerRecipe(recipe -> recipe.matchInputs(getInputHandler().getStacks()), level)
                .ifPresent(recipes -> {
                    if (currentRecipe == null || !currentRecipe.equals(recipes)) {
                        setProgress(0);
                        setRecipe(recipes);
                    }
                });
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.combinerEnergyPerTick.get()
                    && (currentRecipe.getOutput().copy().getCount() + output.copy().getCount()) <= currentRecipe.getOutput().copy().getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(output.copy(), currentRecipe.getOutput().copy()) || output.isEmpty())
                    && currentRecipe.matchInputs(getInputHandler().getStacks());
        }
        return false;
    }

    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getOutputHandler().setOrIncrement(0, currentRecipe.getOutput().copy());
            for (int i = 0; i < currentRecipe.getInput().size(); i++) {
                for (int j = 0; j < getInputHandler().getStacks().size(); j++) {
                    if (currentRecipe.getInput().get(i).matches(getInputHandler().getStackInSlot(j))) {
                        getInputHandler().decrementSlot(j, currentRecipe.getInput().get(i).getCount());
                        break;
                    }
                }
            }
        }
        getEnergyHandler().extractEnergy(Config.Common.combinerEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        currentRecipe = (CombinerRecipe) pRecipe;
    }

    @Override
    public CombinerRecipe getRecipe() {
        return currentRecipe;
    }

    public List<CombinerRecipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<CombinerRecipe> pRecipes) {
        this.recipes = pRecipes;
    }

    protected int getSelectedRecipeIndex() {
        return selectedRecipeIndex;
    }

    protected void setSelectedRecipeIndex(int pIndex) {
        this.selectedRecipeIndex = pIndex;
    }

    protected String getEditBoxText() {
        return editBoxText;
    }

    protected void setEditBoxText(String pText) {
        editBoxText = pText;
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.combinerEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeInputHandler() {
        return new ProcessingSlotHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!isEmpty()) {
                    updateRecipe();
                }
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @NotNull ItemStack pItemStack) {
                if (currentRecipe != null && isRecipeLocked()) {
                    boolean notContained = this.getStacks().stream().noneMatch(itemStack -> ItemStack.isSameItemSameTags(itemStack, pItemStack));
                    boolean inputRequired = currentRecipe.getInput().stream()
                            .map(IngredientStack::getIngredient)
                            .anyMatch(ingredient -> ingredient.test(pItemStack));
                    return notContained && inputRequired;
                }
                return super.isItemValid(pSlot, pItemStack);
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeOutputHandler() {
        return new ProcessingSlotHandler( 1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("maxProgress", maxProgress);
        pTag.putString("editBoxText", editBoxText);
        pTag.putInt("selectedRecipe", selectedRecipeIndex);
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        editBoxText = pTag.getString("editBoxText");
        selectedRecipeIndex = pTag.getInt("selectedRecipe");
        if (level != null) {
            RecipeRegistry.getCombinerRecipe(
                    recipe -> recipe.getId().equals(ResourceLocation.tryParse(pTag.getString("recipeId"))),
                    level).ifPresent(this::setRecipe);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CombinerMenu(pContainerId, pInventory, this);
    }
}
