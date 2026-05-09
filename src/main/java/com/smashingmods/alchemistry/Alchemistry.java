package com.smashingmods.alchemistry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerScreen;
import com.smashingmods.alchemistry.common.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.common.block.compactor.CompactorScreen;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverScreen;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerScreen;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerScreen;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierScreen;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemistry.registry.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Alchemistry.MODID)
public class Alchemistry {

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alchemistry";
    public static final PacketHandler PACKET_HANDLER = new PacketHandler();

    public Alchemistry(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(BlockEntityRegistry::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Config.loadConfig(Config.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve("alchemistry-common.toml"));

        Registry.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(RecipeRegistry::postReload);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PACKET_HANDLER.register(event.registrar(MODID));
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.ATOMIZER_MENU.get(), AtomizerScreen::new);
        event.register(MenuRegistry.COMPACTOR_MENU.get(), CompactorScreen::new);
        event.register(MenuRegistry.COMBINER_MENU.get(), CombinerScreen::new);
        event.register(MenuRegistry.DISSOLVER_MENU.get(), DissolverScreen::new);
        event.register(MenuRegistry.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(MenuRegistry.FISSION_CONTROLLER_MENU.get(), FissionControllerScreen::new);
        event.register(MenuRegistry.FUSION_CONTROLLER_MENU.get(), FusionControllerScreen::new);
    }
}
