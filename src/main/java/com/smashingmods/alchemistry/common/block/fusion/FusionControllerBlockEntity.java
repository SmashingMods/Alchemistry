package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemistry.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorType;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FusionControllerBlockEntity extends AbstractReactorBlockEntity {

    private FusionRecipe currentRecipe;
    private ResourceLocation recipeId;
    private boolean autoBalanced = false;

    public FusionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setReactorType(ReactorType.FUSION);
        setEnergyPerTick(Config.Common.fusionEnergyPerTick.get());
        setMaxProgress(Config.Common.fusionTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getFusionRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    @Override
    public void tick() {
        if (isAutoBalanced()) {
            autoBalance();
        }
        super.tick();
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getInputHandler().isEmpty() && !isRecipeLocked()) {
            Predicate<FusionRecipe> recipePredicate = recipe -> {
                ItemStack input1 = getInputHandler().getStackInSlot(0);
                ItemStack input2 = getInputHandler().getStackInSlot(1);
                return ItemStack.isSameItemSameTags(recipe.getInput1(), input1) && ItemStack.isSameItemSameTags(recipe.getInput2(), input2)
                        || ItemStack.isSameItemSameTags(recipe.getInput2(), input1) && ItemStack.isSameItemSameTags(recipe.getInput1(), input2);
            };

            RecipeRegistry.getFusionRecipe(recipePredicate, level)
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
            FusionRecipe tempRecipe = currentRecipe.copy();
            ItemStack input1 = getInputHandler().getStackInSlot(0);
            ItemStack input2 = getInputHandler().getStackInSlot(1);
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.fusionEnergyPerTick.get()
                    && (((ItemStack.isSameItemSameTags(input1, tempRecipe.getInput1()) && input1.getCount() >= tempRecipe.getInput1().getCount())
                        && (ItemStack.isSameItemSameTags(input2, tempRecipe.getInput2()) && input2.getCount() >= tempRecipe.getInput2().getCount()))
                    || ((ItemStack.isSameItemSameTags(input1, tempRecipe.getInput2()) && input1.getCount() >= tempRecipe.getInput2().getCount())
                        && (ItemStack.isSameItemSameTags(input2, tempRecipe.getInput1()) && input2.getCount() >= tempRecipe.getInput1().getCount())))
                    && ((ItemStack.isSameItemSameTags(output, tempRecipe.getOutput()) || output.isEmpty()) && (tempRecipe.getOutput().getCount() + output.getCount()) <= tempRecipe.getOutput().getMaxStackSize());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            FusionRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getInputHandler().decrementSlot(0, tempRecipe.getInput1().getCount());
            getInputHandler().decrementSlot(1, tempRecipe.getInput2().getCount());
            getOutputHandler().setOrIncrement(0, tempRecipe.getOutput());
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof FusionRecipe fusionRecipe) {
            currentRecipe = fusionRecipe;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public FusionRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<FusionRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getFusionRecipes(level));
        }
        return new LinkedList<>();
    }

    public void autoBalance() {
        if (currentRecipe != null && !getInputHandler().isEmpty()) {
            if (ItemStack.isSameItemSameTags(currentRecipe.getInput1(), currentRecipe.getInput2())) {

                ItemStack slot0 = getInputHandler().getStackInSlot(0);
                ItemStack slot1 = getInputHandler().getStackInSlot(1);

                if (slot0.isEmpty() && slot1.getCount() >= 2) {
                    int split = slot1.getCount() / 2;
                    int remainder = slot1.getCount() % 2;
                    getInputHandler().decrementSlot(1, split + remainder);
                    getInputHandler().setOrIncrement(0, new ItemStack(slot1.getItem(), split + remainder));
                } else if (slot1.isEmpty() && slot0.getCount() >= 2) {
                    int split = slot0.getCount() / 2;
                    int remainder = slot0.getCount() % 2;
                    getInputHandler().decrementSlot(0, split + remainder);
                    getInputHandler().setOrIncrement(1, new ItemStack(slot0.getItem(), split + remainder));
                } else if (slot0.getCount() > slot1.getCount() + 1 && slot1.getCount() < slot1.getMaxStackSize()) {
                    int difference = (slot0.getCount() - slot1.getCount()) / 2;
                    getInputHandler().decrementSlot(0, difference);
                    getInputHandler().incrementSlot(1, difference);
                } else if (slot1.getCount() > slot0.getCount() + 1 && slot0.getCount() < slot0.getMaxStackSize()) {
                    int difference = (slot1.getCount() - slot0.getCount()) / 2;
                    getInputHandler().decrementSlot(1, difference);
                    getInputHandler().incrementSlot(0, difference);
                }
            }
        }
    }

    public boolean isAutoBalanced() {
        return autoBalanced;
    }

    public void setAutoBalanced(boolean pAutoBalanced) {
        this.autoBalanced = pAutoBalanced;
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.fusionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeInputHandler() {
        return new ProcessingSlotHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!isEmpty()) {
                    updateRecipe();
                }
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @Nonnull ItemStack pItemStack) {

                if (level != null && !level.isClientSide() && autoBalanced && !isRecipeLocked()) {
                    ItemStack slot0 = pSlot == 0 ? pItemStack : getStackInSlot(0);
                    ItemStack slot1 = pSlot == 1 ? pItemStack : getStackInSlot(1);

                    Predicate<FusionRecipe> recipePredicate = recipe -> {
                        if (slot0.getItem() instanceof ElementItem && slot1.isEmpty()) {
                            return ItemStack.isSameItemSameTags(recipe.getInput1(), slot0) && ItemStack.isSameItemSameTags(recipe.getInput2(), slot0);
                        } else if (slot1.getItem() instanceof ElementItem && slot0.isEmpty()) {
                            return ItemStack.isSameItemSameTags(recipe.getInput1(), slot1) && ItemStack.isSameItemSameTags(recipe.getInput2(), slot1);
                        }
                        return false;
                    };

                    RecipeRegistry.getFusionRecipe(recipePredicate, level).ifPresent(recipe -> {
                        if (!currentRecipe.equals(recipe)) {
                            setProgress(0);
                            setRecipe(recipe);
                            autoBalance();
                        }
                    });
                }

                if (currentRecipe != null && isRecipeLocked()) {
                    return Stream.of(currentRecipe.getInput1(), currentRecipe.getInput2())
                            .anyMatch(itemStack -> ItemStack.isSameItemSameTags(pItemStack, itemStack));
                }
                return pItemStack.getItem() instanceof ElementItem;
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
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putBoolean("autoBalanced", autoBalanced);
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        setAutoBalanced(pTag.getBoolean("autoBalanced"));
        if (level != null && level.isClientSide()) {
            RecipeRegistry.getFusionRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(recipe -> {
                if (!recipe.equals(currentRecipe)) {
                    setRecipe(recipe);
                    PacketHandler.INSTANCE.sendToServer(new SetRecipePacket(getBlockPos(), recipe.getId(), recipe.getGroup()));
                }
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FusionControllerMenu(pContainerId, pInventory, this);
    }
}
