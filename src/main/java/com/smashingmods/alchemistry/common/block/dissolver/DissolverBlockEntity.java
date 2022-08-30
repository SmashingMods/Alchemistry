package com.smashingmods.alchemistry.common.block.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DissolverBlockEntity extends AbstractInventoryBlockEntity {

    private final int maxProgress = Config.Common.dissolverTicksPerOperation.get();
    private DissolverRecipe currentRecipe;
    private final NonNullList<ItemStack> internalBuffer = NonNullList.createWithCapacity(64);

    public DissolverBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isProcessingPaused()) {
            processBuffer();
        }
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !isRecipeLocked()) {
            RecipeRegistry.getDissolverRecipe(recipe -> recipe.matches(getInputHandler().getStackInSlot(0)), level)
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
            ItemStack input = getInputHandler().getStackInSlot(0).copy();
            return getEnergyHandler().getEnergyStored() >= Config.Common.dissolverEnergyPerTick.get()
                    && (currentRecipe.matches(input) && input.getCount() >= currentRecipe.getInput().getCount())
                    && internalBuffer.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getInputHandler().decrementSlot(0, currentRecipe.getInput().getCount());
            internalBuffer.addAll(currentRecipe.getOutput().calculateOutput());
        }
        getEnergyHandler().extractEnergy(Config.Common.dissolverEnergyPerTick.get(), false);
        setChanged();
    }

    private void processBuffer() {
        for (int i = 0; i < internalBuffer.size(); i++) {
            ItemStack bufferStack = internalBuffer.get(i).copy();
            for (int j = 0; j < getOutputHandler().getStacks().size(); j++) {
                ItemStack slotStack = getOutputHandler().getStackInSlot(j).copy();
                if (slotStack.isEmpty() || (ItemStack.isSameItemSameTags(bufferStack, slotStack) && bufferStack.getCount() + slotStack.getCount() <= slotStack.getMaxStackSize())) {
                    getOutputHandler().setOrIncrement(j, bufferStack);
                    internalBuffer.remove(i);
                    break;
                }
            }
        }
        setCanProcess(canProcessRecipe());
        setChanged();
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        currentRecipe = (DissolverRecipe) pRecipe;
    }

    @Override
    public DissolverRecipe getRecipe() {
        return currentRecipe;
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.dissolverEnergyCapacity.get()) {
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
                if (!isEmpty()) {
                    updateRecipe();
                }
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @NotNull ItemStack pItemStack) {
                if (currentRecipe != null && isRecipeLocked()) {
                    return currentRecipe.getInput().matches(pItemStack);
                }
                return super.isItemValid(pSlot, pItemStack);
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeOutputHandler() {
        return new ProcessingSlotHandler(10) {
            @Override
            public boolean isItemValid(int pSlot, ItemStack pItemStack) {
                return false;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("maxProgress", maxProgress);
        ListTag bufferTag = new ListTag();
        internalBuffer.stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .forEach(itemStack -> bufferTag.add(itemStack.save(new CompoundTag())));
        pTag.put("buffer", bufferTag);
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ListTag bufferTag = pTag.getList("buffer", 10);
        bufferTag.stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(CompoundTag.class::cast)
                .map(ItemStack::of)
                .forEach(internalBuffer::add);

        if (level != null) {
            RecipeRegistry.getDissolverRecipe(
                    recipe -> recipe.getId().equals(ResourceLocation.tryParse(pTag.getString("recipeId"))),
                    level).ifPresent(this::setRecipe);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new DissolverMenu(pContainerId, pInventory, this);
    }
}
