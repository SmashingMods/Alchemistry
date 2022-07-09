package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface InventoryBlockEntity {

    CustomItemStackHandler getInputHandler();

    CustomItemStackHandler getOutputHandler();

    AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pHandler);

    AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler);

    CombinedInvWrapper getAutomationInventory();

    CustomItemStackHandler initializeInputHandler();

    CustomItemStackHandler initializeOutputHandler();

    void getDrops();
}
