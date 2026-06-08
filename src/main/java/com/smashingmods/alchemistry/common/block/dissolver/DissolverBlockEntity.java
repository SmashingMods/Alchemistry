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
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.ListIterator;

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
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getDissolverRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
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
        // Drain the buffer into the output handler. A single buffered stack can exceed a slot's max size --
        // the recipe data stores >64 outputs (e.g. iron_block -> chemlib:iron x144) as one oversized stack --
        // so insertItemStacked may only place part of it when the output is full or the count overflows the
        // handler's capacity. Retain whatever it could not insert in the buffer so it is delivered on a later
        // tick once the output drains; never drop the remainder. A ListIterator lets us remove or replace the
        // current entry in place without the index skip a remove() inside a for(i++) loop would cause.
        ListIterator<ItemStack> iterator = internalBuffer.listIterator();
        while (iterator.hasNext()) {
            ItemStack bufferStack = iterator.next();
            valid = true;
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(getOutputHandler(), bufferStack.copy(), false);
            valid = false;
            if (remainder.isEmpty()) {
                iterator.remove();
            } else {
                iterator.set(remainder);
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
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        ListTag bufferTag = new ListTag();
        internalBuffer.stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .forEach(itemStack -> bufferTag.add(itemStack.save(pRegistries, new CompoundTag())));
        pTag.put("buffer", bufferTag);
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        ListTag bufferTag = pTag.getList("buffer", 10);
        bufferTag.stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(CompoundTag.class::cast)
                .map(tag -> ItemStack.parseOptional(pRegistries, tag))
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
            Containers.dropContents(pLevel, pBlockPos, internalBuffer);
        }
        super.dropContents(pLevel, pBlockPos);
    }
}
