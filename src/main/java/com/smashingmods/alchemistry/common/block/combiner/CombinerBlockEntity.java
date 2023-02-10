package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class CombinerBlockEntity extends AbstractSearchableBlockEntity {

    private CombinerRecipe currentRecipe;
    private ResourceLocation recipeId;

    private LazyOptional<IItemHandler> lazyInputHandler;
    private LazyOptional<IItemHandler> lazyOutputHandler;

    public CombinerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.combinerEnergyPerTick.get());
        setMaxProgress(Config.Common.combinerTicksPerOperation.get());

        this.lazyInputHandler = LazyOptional.of(() -> this.getInputHandler());
        this.lazyOutputHandler = LazyOptional.of(() -> this.getOutputHandler());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getCombinerRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getInputHandler().isEmpty() && !isRecipeLocked()) {
            RecipeRegistry.getCombinerRecipe(recipe -> recipe.matchInputs(getInputHandler().getStacks()), level)
                .ifPresent(recipe -> {
                    if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                        setProgress(0);
                        setRecipe(recipe.copy());
                    }
                });
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            CombinerRecipe tempRecipe = currentRecipe.copy();
            ItemStack output = getOutputHandler().getStackInSlot(0).copy();
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && (tempRecipe.getOutput().getCount() + output.getCount()) <= tempRecipe.getOutput().getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(output, tempRecipe.getOutput()) || output.isEmpty())
                    && tempRecipe.matchInputs(getInputHandler().getStacks());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            CombinerRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getOutputHandler().setOrIncrement(0, tempRecipe.getOutput());
            for (int i = 0; i < tempRecipe.getInput().size(); i++) {
                for (int j = 0; j < getInputHandler().getStacks().size(); j++) {
                    if (tempRecipe.getInput().get(i).matches(getInputHandler().getStackInSlot(j))) {
                        getInputHandler().decrementSlot(j, tempRecipe.getInput().get(i).getCount());
                        break;
                    }
                }
            }
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof CombinerRecipe combinerRecipe) {
            currentRecipe = combinerRecipe;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CombinerRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<CombinerRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getCombinerRecipes(level));
        }
        return new LinkedList<>();
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
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @NotNull ItemStack pItemStack) {
                if (currentRecipe != null && isRecipeLocked()) {
                    boolean inputRequired = currentRecipe.getInput().stream()
                            .map(IngredientStack::getIngredient)
                            .anyMatch(ingredient -> ingredient.test(pItemStack));

                    if (!inputRequired) {
                        return false;
                    }

                    boolean notContained = this.getStacks().stream().noneMatch(itemStack -> ItemStack.isSameItemSameTags(itemStack, pItemStack));
                    if (notContained) {
                        return true;
                    }

                    boolean sameItemAtSlot = ItemStack.isSameItemSameTags(pItemStack, this.getStacks().get(pSlot));
                    return sameItemAtSlot;
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
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        if (level != null && level.isClientSide()) {
            RecipeRegistry.getCombinerRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(recipe -> {
                if (!recipe.equals(currentRecipe)) {
                    setRecipe(recipe);
                    Alchemistry.PACKET_HANDLER.sendToServer(new SetRecipePacket(getBlockPos(), recipe.getId(), recipe.getGroup()));
                }
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CombinerMenu(pContainerId, pInventory, this);
    }

    // wtf error on nonnuull build
    @javax.annotation.Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@javax.annotation.Nonnull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == ForgeCapabilities.ITEM_HANDLER) {
            if (pDirection == Direction.DOWN)
                return this.lazyOutputHandler.cast();
            else if (pDirection == Direction.UP)
                return this.lazyInputHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }
}
