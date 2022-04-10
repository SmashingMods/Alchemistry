package com.smashingmods.alchemistry.setup;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemistry.network.Messages;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("Alchemistry") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.ATOMIZER_BLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent e) {
        Alchemistry.proxy.init();
        Messages.register();
        Registration.ATOMIZER_TYPE = RecipeType.register(Alchemistry.MODID + ":atomizer");
        Registration.COMBINER_TYPE = RecipeType.register(Alchemistry.MODID + ":combiner");
        Registration.DISSOLVER_TYPE = RecipeType.register(Alchemistry.MODID + ":dissolver");
        Registration.EVAPORATOR_TYPE = RecipeType.register(Alchemistry.MODID + ":evaporator");
        Registration.FISSION_TYPE = RecipeType.register(Alchemistry.MODID + ":fission");
        Registration.LIQUIFIER_TYPE = RecipeType.register(Alchemistry.MODID + ":liquifier");
    }
}