package com.smashingmods.alchemistry.common.block.atomizer;

import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AtomizerMenu extends AbstractAlchemistryMenu {

    public AtomizerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())), new SimpleContainerData(6));
    }

    protected AtomizerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(MenuRegistry.ATOMIZER_MENU.get(), pContainerId, pInventory, pBlockEntity, pContainerData, 3);
        checkContainerSize(pInventory, 3);
        AbstractAlchemistryBlockEntity blockEntity = ((AbstractAlchemistryBlockEntity) pBlockEntity);
        this.addSlot(new SlotItemHandler(((InventoryBlockEntity) blockEntity).getOutputHandler(), 0, 116, 35));
        this.addSlot(new SlotItemHandler(new ItemStackHandler(), 0, 8, 21));
        this.addSlot(new SlotItemHandler(new ItemStackHandler(), 0, 8, 51));
    }

    @Override
    public boolean stillValid(@Nonnull Player pPlayer) {
        return true;
    }
}
