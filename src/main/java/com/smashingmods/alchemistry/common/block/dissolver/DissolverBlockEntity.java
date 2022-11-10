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
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class DissolverBlockEntity extends AbstractInventoryBlockEntity {

    private DissolverRecipe currentRecipe;
    private ResourceLocation recipeId;
    private final NonNullList<ItemStack> internalBuffer = NonNullList.createWithCapacity(64);

    public DissolverBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.dissolverEnergyPerTick.get());
        setMaxProgress(Config.Common.dissolverTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getDissolverRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
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
            DissolverRecipe tempRecipe = currentRecipe.copy();
            ItemStack input = getInputHandler().getStackInSlot(0).copy();
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && (tempRecipe.matches(input) && input.getCount() >= tempRecipe.getInput().getCount())
                    && internalBuffer.isEmpty();
        } else {
            return false;
        }
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
                if (slotStack.isEmpty() || (ItemStack.isSameItemSameTags(bufferStack, slotStack) && bufferStack.getCount() + slotStack.getCount() <= slotStack.getMaxStackSize())) {
                    ItemHandlerHelper.insertItemStacked(getOutputHandler(), bufferStack, false);
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
        return new ProcessingSlotHandler(12) {

            private boolean valid = false;

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                valid = true;
                ItemStack itemStack = super.insertItem(slot, stack, simulate);
                valid = false;
                return itemStack;
            }

            @Override
            public boolean isItemValid(int pSlot, ItemStack pItemStack) {
                return valid;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
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
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        ListTag bufferTag = pTag.getList("buffer", 10);
        bufferTag.stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(CompoundTag.class::cast)
                .map(ItemStack::of)
                .forEach(internalBuffer::add);

        if (level != null && level.isClientSide()) {
            RecipeRegistry.getDissolverRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(recipe -> {
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
            Containers.dropContents(pLevel, getBlockPos(), internalBuffer);
        }
        super.dropContents(pLevel, pBlockPos);
    }
}
