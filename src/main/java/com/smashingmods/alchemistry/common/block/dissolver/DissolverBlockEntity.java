package com.smashingmods.alchemistry.common.block.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class DissolverBlockEntity extends AbstractInventoryBlockEntity {

    private DissolverRecipe currentRecipe;
    private ResourceLocation recipeId;
    private final NonNullList<ItemStack> internalBuffer = NonNullList.createWithCapacity(64);
    private boolean valid = false;

    public DissolverBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.dissolverEnergyPerTick.get());
        setMaxProgress(Config.Common.dissolverTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide() && recipeId != null) {
            RecipeRegistry.getDissolverRecipe(recipe -> recipeId.equals(recipe.getId()), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    @Override
    public void tick() {
        if (!isProcessingPaused() && (!getInputHandler().getStackInSlot(0).isEmpty() || !internalBuffer.isEmpty())) {
            super.tick();
            processBuffer();
        }
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !isRecipeLocked() && !getInputHandler().getStackInSlot(0).isEmpty()) {
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
            DissolverRecipe tempRecipe = currentRecipe.copy();
            ItemStack input = getInputHandler().getStackInSlot(0).copy();
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && (tempRecipe.matches(input) && input.getCount() >= tempRecipe.getInput().getCount())
                    && internalBuffer.isEmpty();
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            DissolverRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getInputHandler().decrementSlot(0, tempRecipe.getInput().getCount());
            internalBuffer.addAll(tempRecipe.getOutput().calculateOutput());
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    private void processBuffer() {
        for (int i = 0; i < internalBuffer.size(); i++) {
            ItemStack bufferStack = internalBuffer.get(i).copy();
            for (int j = 0; j < getOutputHandler().getStacks().size(); j++) {
                ItemStack slotStack = getOutputHandler().getStackInSlot(j).copy();
                if (slotStack.isEmpty() || (ItemStack.isSameItemSameComponents(bufferStack, slotStack) && bufferStack.getCount() + slotStack.getCount() <= slotStack.getMaxStackSize())) {
                    valid = true;
                    ItemHandlerHelper.insertItemStacked(getOutputHandler(), bufferStack, false);
                    valid = false;
                    internalBuffer.remove(i);
                    break;
                }
            }
        }
        setCanProcess(canProcessRecipe());
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof DissolverRecipe dissolverRecipe) {
            currentRecipe = dissolverRecipe;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DissolverRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<DissolverRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getDissolverRecipes(level));
        }
        return new LinkedList<>();
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
            protected void onContentsChanged(int pSlot) {
                updateRecipe();
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @Nonnull ItemStack pItemStack) {
                if (currentRecipe != null && isRecipeLocked()) {
                    return currentRecipe.getInput().matches(pItemStack);
                }
                return super.isItemValid(pSlot, pItemStack);
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeOutputHandler() {
        return new ProcessingSlotHandler(12) {

            @Nonnull
            @Override
            public ItemStack insertItem(int pSlot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(pSlot, stack, simulate);
            }

            @Override
            public boolean isItemValid(int pSlot, ItemStack pItemStack) {
                return valid;
            }

            @Override
            protected void onContentsChanged(int pSlot) {
                setChanged();
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pProvider) {
        ListTag bufferTag = new ListTag();
        for (ItemStack itemStack : internalBuffer) {
            if (!itemStack.isEmpty()) {
                Tag saved = itemStack.save(pProvider);
                if (saved instanceof CompoundTag compoundTag) {
                    bufferTag.add(compoundTag);
                }
            }
        }
        pTag.put("buffer", bufferTag);
        if (currentRecipe != null && currentRecipe.getId() != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag, pProvider);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pProvider) {
        super.loadAdditional(pTag, pProvider);
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        ListTag bufferTag = pTag.getList("buffer", Tag.TAG_COMPOUND);
        for (int i = 0; i < bufferTag.size(); i++) {
            CompoundTag entry = bufferTag.getCompound(i);
            ItemStack.parse(pProvider, entry).ifPresent(internalBuffer::add);
        }

        if (level != null && level.isClientSide() && recipeId != null) {
            RecipeRegistry.getDissolverRecipe(recipe -> recipeId.equals(recipe.getId()), level).ifPresent(recipe -> {
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
        return new DissolverMenu(pContainerId, pInventory, this);
    }

    @Override
    public void dropContents(Level pLevel, BlockPos pBlockPos) {
        if (!pLevel.isClientSide()) {
            Containers.dropContents(pLevel, pBlockPos, internalBuffer);
        }
        super.dropContents(pLevel, pBlockPos);
    }
}
