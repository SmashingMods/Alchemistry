package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class CompactorBlockEntity extends AbstractSearchableBlockEntity {

    private CompactorRecipe currentRecipe;
    private ResourceLocation recipeId;

    public CompactorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.compactorEnergyPerTick.get());
        setMaxProgress(Config.Common.compactorTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getCompactorRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getInputHandler().isEmpty() && !isRecipeLocked()) {
            // The player's selector choice wins while it still matches the input; only machines the player
            // never chose for fall back to the first matching recipe in sorted order. The compactor lost
            // selections on the very insert: its input handler's onContentsChanged calls updateRecipe, so
            // cellulose arriving replaced a selected jungle log with acacia (the first cellulose match)
            // before the next tick.
            RecipeRegistry.getCompactorRecipe(recipe -> isSelectedRecipe(recipe) && recipe.getInput().matches(getInputHandler().getStackInSlot(0)), level)
                .or(() -> RecipeRegistry.getCompactorRecipe(recipe -> recipe.getInput().matches(getInputHandler().getStackInSlot(0)), level))
                .ifPresent(recipe -> {
                    if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                        setProgress(0);
                        setRecipe(recipe);
                    }
                });
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = getInputHandler().getStackInSlot(0);
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && (currentRecipe.getInput().matches(input) && input.getCount() >= currentRecipe.getInput().getCount())
                    && (currentRecipe.getOutput().getCount() + output.getCount()) <= currentRecipe.getOutput().getMaxStackSize()
                    && (ItemStack.isSameItemSameComponents(output, currentRecipe.getOutput()) || output.isEmpty());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            CompactorRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getOutputHandler().setOrIncrement(0, tempRecipe.getOutput());
            getInputHandler().decrementSlot(0, tempRecipe.getInput().getCount());
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof CompactorRecipe compactorRecipe) {
            currentRecipe = compactorRecipe;
        } else if (pRecipe != null) {
            // A wrong-typed recipe reaching this machine means a recipe lookup went wrong upstream;
            // dropping it silently made that failure mode invisible in the field.
            Alchemistry.LOGGER.error("Refusing to set recipe {} on the compactor at {}: not a CompactorRecipe", pRecipe.getId(), getBlockPos());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompactorRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<CompactorRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getCompactorRecipes(level));
        }
        return new LinkedList<>();
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.compactorEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeInputHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                updateRecipe();
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @Nonnull ItemStack pItemStack) {
                if (level != null && !level.isClientSide()) {
                    if (pSlot == 0 && currentRecipe != null && isRecipeLocked()) {
                        return currentRecipe.getInput().matches(pItemStack);
                    }
                }
                return super.isItemValid(pSlot, pItemStack);
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeOutputHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        if (level != null && level.isClientSide()) {
            // Display-only mirror of the server's recipe. This used to also echo a SetRecipePacket back to
            // the server, which was redundant (onLoad restores the server's recipe from its own save) and,
            // now that a selection is remembered, would promote every server auto-pick into a player
            // selection whenever the client's copy lagged a menu sync.
            RecipeRegistry.getCompactorRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CompactorMenu(pContainerId, pInventory, this);
    }
}
