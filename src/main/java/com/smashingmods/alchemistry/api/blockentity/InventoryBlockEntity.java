package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.ModItemStackHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface InventoryBlockEntity {

    ModItemStackHandler getInputHandler();

    ModItemStackHandler getOutputHandler();

    AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pHandler);

    AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler);

    CombinedInvWrapper getAutomationInventory();

    ModItemStackHandler initializeInputHandler();

    ModItemStackHandler initializeOutputHandler();

    void getDrops();
}
