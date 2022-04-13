package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface InventoryBlockEntity {

    CustomStackHandler initInputHandler();

    CustomStackHandler initOutputHandler();

    AutomationStackHandler initAutomationInputHandler(IItemHandlerModifiable inv);

    AutomationStackHandler initAutomationOutputHandler(IItemHandlerModifiable inv);

    @SuppressWarnings("unused")
    CustomStackHandler getInputHandler();

    @SuppressWarnings("unused")
    CustomStackHandler getOutputHandler();

    LazyOptional<IItemHandler> getExternalInventory();

    @SuppressWarnings("unused")
    CombinedInvWrapper getAutomationInventory();
}