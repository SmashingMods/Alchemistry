package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.*;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            Optional<CombinerRecipe> combinerRecipe = RecipeRegistry.getRecipesByType(RecipeRegistry.COMBINER_TYPE, level).stream().filter(recipe -> recipe.matchInputs(getInputHandler())).findFirst();
            combinerRecipe.ifPresent(this::setCurrentRecipe);
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.combinerEnergyPerTick.get()
                    && (currentRecipe.getOutput().getCount() + output.getCount()) <= currentRecipe.getOutput().getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(output, currentRecipe.getOutput()) || output.isEmpty())
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
            for (int index = 0; index < currentRecipe.getInput().size(); index++) {
                ItemStack itemStack = currentRecipe.getInput().get(index);
                if (itemStack != null && !itemStack.isEmpty()) {
                    getInputHandler().decrementSlot(index, itemStack.getCount());
                }
            }
        }
        getEnergyHandler().extractEnergy(Config.Common.combinerEnergyPerTick.get(), false);
        setChanged();
    }

    public List<CombinerRecipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<CombinerRecipe> pRecipes) {
        this.recipes = pRecipes;
    }

    public CombinerRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }

    public void setCurrentRecipe(CombinerRecipe pRecipe) {
        this.currentRecipe = pRecipe;
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
