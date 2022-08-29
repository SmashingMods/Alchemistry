package com.smashingmods.alchemistry.api.blockentity.processing;

import com.smashingmods.alchemistry.api.storage.AutomationSlotHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface InventoryBlockEntity {

    ProcessingSlotHandler getInputHandler();

    ProcessingSlotHandler initializeInputHandler();

    ProcessingSlotHandler getOutputHandler();

    ProcessingSlotHandler initializeOutputHandler();

    AutomationSlotHandler getAutomationInputHandler(IItemHandlerModifiable pHandler);

    AutomationSlotHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler);

    CombinedInvWrapper getAutomationInventory();
}
