package com.smashingmods.alchemistry.common.block.dissolver;

import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DissolverMenu extends AbstractAlchemistryMenu {

    protected final ContainerData containerData;
    private final DissolverBlockEntity blockEntity;
    private final CustomStackHandler inputHandler;
    private final CustomStackHandler outputHandler;

    public DissolverMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())), new SimpleContainerData(4));
    }

    protected DissolverMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(MenuRegistry.DISSOLVER_MENU.get(), pContainerId, pInventory, pBlockEntity, pContainerData, 1);

        this.containerData = pContainerData;
        this.blockEntity = (DissolverBlockEntity) pBlockEntity;
        this.inputHandler = ((InventoryBlockEntity) blockEntity).getInputHandler();
        this.outputHandler = ((InventoryBlockEntity) blockEntity).getOutputHandler();

        // input
        addSlots(SlotItemHandler::new, inputHandler, 1, 1, 0, 84, 12);
        // output 2x5 grid
        addSlots(SlotItemHandler::new, outputHandler, 2, 5, 0, 48, 68);
    }

    @Override
    public void addPlayerInventorySlots(Inventory pInventory) {
        addSlots(Slot::new, pInventory, 3, 9, 9,12, 113);
        addSlots(Slot::new, pInventory, 1, 9, 0,12, 171);
    }

    @Override
    public boolean stillValid(@Nonnull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(this.getBlockEntity().getLevel()), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.DISSOLVER.get());
    }
}
