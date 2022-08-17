package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CombinerBlockEntity extends AbstractInventoryBlockEntity {

    protected final ContainerData data;
    private final int maxProgress = Config.Common.combinerTicksPerOperation.get();
    private List<CombinerRecipe> recipes = new ArrayList<>();
    private CombinerRecipe currentRecipe;
    private int selectedRecipe = -1;
    private String editBoxText = "";

    public CombinerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> getProgress();
                    case 1 -> maxProgress;
                    case 2 -> getEnergyHandler().getEnergyStored();
                    case 3 -> getEnergyHandler().getMaxEnergyStored();
                    case 4 -> selectedRecipe;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> setProgress(pValue);
                    case 2 -> getEnergyHandler().setEnergy(pValue);
                    case 4 -> selectedRecipe = pValue;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (currentRecipe == null) {
                RecipeRegistry.getRecipesByType(RecipeRegistry.COMBINER_TYPE, level).stream()
                        .filter(recipe -> recipe.matchInputs(getInputHandler()))
                        .findFirst()
                        .ifPresent(recipe -> {
                            if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                                setProgress(0);
                                setRecipe(recipe);
                            }
                        });
            }
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.combinerEnergyPerTick.get()
                    && (currentRecipe.getOutput().copy().getCount() + output.copy().getCount()) <= currentRecipe.getOutput().copy().getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(output.copy(), currentRecipe.getOutput().copy()) || output.isEmpty())
                    && currentRecipe.matchInputs(getInputHandler());
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
                    if (ItemStack.isSameItemSameTags(currentRecipe.getInput().get(i), getInputHandler().getStacks().get(j))) {
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

    protected String getEditBoxText() {
        return editBoxText;
    }

    protected void setEditBoxText(String pText) {
        editBoxText = pText;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.combinerEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeInputHandler() {
        return new CustomItemStackHandler(4);
    }

    @Override
    public CustomItemStackHandler initializeOutputHandler() {
        return new CustomItemStackHandler( 1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putString("editBoxText", editBoxText);
        pTag.putInt("selectedRecipe", selectedRecipe);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        editBoxText = pTag.getString("editBoxText");
        selectedRecipe = pTag.getInt("selectedRecipe");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CombinerMenu(pContainerId, pInventory, this, this.data);
    }
}
