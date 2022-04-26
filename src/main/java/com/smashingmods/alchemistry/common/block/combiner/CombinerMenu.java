package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CombinerMenu extends AbstractAlchemistryMenu {

    public CombinerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())), new SimpleContainerData(4));
    }

    protected CombinerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(MenuRegistry.COMBINER_MENU.get(), pContainerId, pInventory, pBlockEntity, pContainerData, 5);

        AbstractAlchemistryBlockEntity blockEntity = ((AbstractAlchemistryBlockEntity) pBlockEntity);

        // input 2x2 grid
        addSlots(SlotItemHandler::new, ((InventoryBlockEntity) blockEntity).getInputHandler(), 2, 2, 0, 26, 33);
        // catalyst/solvent
        addSlots(SlotItemHandler::new, ((InventoryBlockEntity) blockEntity).getInputHandler(), 1, 1, 4, 71, 15);
        // ouput
        addSlots(SlotItemHandler::new, ((InventoryBlockEntity) blockEntity).getOutputHandler(), 1, 1, 0, 98, 42);
    }

    @Override
    public boolean stillValid(@Nonnull Player pPlayer) {
        Objects.requireNonNull(this.getBlockEntity().getLevel());
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()), pPlayer, BlockRegistry.COMBINER.get());
    }
}
