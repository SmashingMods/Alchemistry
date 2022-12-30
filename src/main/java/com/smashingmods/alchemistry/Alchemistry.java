package com.smashingmods.alchemistry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerScreen;
import com.smashingmods.alchemistry.common.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.common.block.compactor.CompactorScreen;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverScreen;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerScreen;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerScreen;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierScreen;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.datagen.DataGenerators;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemistry.registry.Registry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Alchemistry.MODID)
public class Alchemistry {

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alchemistry";
    public static final PacketHandler PACKET_HANDLER = new PacketHandler().register();

    public Alchemistry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(this::clientSetupEvent);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Config.loadConfig(Config.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve("alchemistry-common.toml"));
        Registry.register();

        // Make sure that `/reload` and world loading wipe the machine recipe cache.
        MinecraftForge.EVENT_BUS.addListener(RecipeRegistry::postReload);
    }

    public void clientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuRegistry.ATOMIZER_MENU.get(), AtomizerScreen::new);
            MenuScreens.register(MenuRegistry.COMPACTOR_MENU.get(), CompactorScreen::new);
            MenuScreens.register(MenuRegistry.COMBINER_MENU.get(), CombinerScreen::new);
            MenuScreens.register(MenuRegistry.DISSOLVER_MENU.get(), DissolverScreen::new);
            MenuScreens.register(MenuRegistry.LIQUIFIER_MENU.get(), LiquifierScreen::new);
            MenuScreens.register(MenuRegistry.FISSION_CONTROLLER_MENU.get(), FissionControllerScreen::new);
            MenuScreens.register(MenuRegistry.FUSION_CONTROLLER_MENU.get(), FusionControllerScreen::new);
        });
    }
}