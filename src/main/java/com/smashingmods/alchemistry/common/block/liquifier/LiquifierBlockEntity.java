package com.smashingmods.alchemistry.common.block.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class LiquifierBlockEntity extends AbstractFluidBlockEntity {

    private LiquifierRecipe currentRecipe;
    private ResourceLocation recipeId;

    public LiquifierBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.liquifierEnergyPerTick.get());
        setMaxProgress(Config.Common.liquifierTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getLiquifierRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getSlotHandler().isEmpty()) {
            RecipeRegistry.getLiquifierRecipe(recipe -> recipe.getInput().matches(getSlotHandler().getStackInSlot(0)), level)
                .ifPresent(recipe -> {
                    if (currentRecipe == null || !currentRecipe.getId().equals(recipe.getId())) {
                        setProgress(0);
                        setRecipe(recipe.copy());
                    }
                });
        }
    }

    @Override
    public boolean canProcessRecipe() {
        ItemStack input = getSlotHandler().getStackInSlot(0);
        if (currentRecipe != null) {
            LiquifierRecipe tempRecipe = currentRecipe.copy();
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && (getFluidStorage().getFluidStack().isFluidEqual(tempRecipe.getOutput()) || getFluidStorage().isEmpty())
                    && (getFluidStorage().getFluidAmount() + tempRecipe.getOutput().getAmount()) < getFluidStorage().getCapacity()
                    && (tempRecipe.getInput().matches(input) && input.getCount() >= tempRecipe.getInput().getCount());
        } else {
            return false;
        }
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            LiquifierRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getSlotHandler().decrementSlot(0, tempRecipe.getInput().getCount());
            getFluidStorage().fill(tempRecipe.getOutput(), IFluidHandler.FluidAction.EXECUTE);
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof LiquifierRecipe liquifierRecipe) {
            currentRecipe = liquifierRecipe;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public LiquifierRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<LiquifierRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getLiquifierRecipes(level));
        }
        return new LinkedList<>();
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.liquifierEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                super.onEnergyChanged();
                setChanged();
            }
        };
    }

    @Override
    public FluidStorageHandler initializeFluidStorage() {
        return new FluidStorageHandler(Config.Common.liquifierFluidCapacity.get(), FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                if (level != null && !level.isClientSide() && isEmpty()) {
                    updateRecipe();
                    setCanProcess(canProcessRecipe());
                }
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeSlotHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!isEmpty()) {
                    updateRecipe();
                }
                setCanProcess(canProcessRecipe());
                setChanged();
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
            RecipeRegistry.getLiquifierRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(recipe -> {
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
        return new LiquifierMenu(pContainerId, pInventory, this);
    }
}
