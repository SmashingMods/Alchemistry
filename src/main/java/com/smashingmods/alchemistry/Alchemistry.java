package com.smashingmods.alchemistry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerScreen;
import com.smashingmods.alchemistry.common.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.common.block.compactor.CompactorScreen;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverScreen;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerScreen;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerScreen;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierScreen;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemistry.registry.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Alchemistry.MODID)
public class Alchemistry {

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alchemistry";

    public Alchemistry(ModContainer modContainer, IEventBus modEventBus) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Config.loadConfig(Config.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve("alchemistry-common.toml"));
        Registry.register(modEventBus);

        // Make sure that `/reload` and world loading wipe the machine recipe cache.
        NeoForge.EVENT_BUS.addListener(RecipeRegistry::postReload);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            // Energy capability for all processing machines
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.FISSION_CONTROLLER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getEnergyHandler());

            // Item handler capability for all processing machines
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.FISSION_CONTROLLER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                    BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getCombinedSlotHandler().getView(side));
            // Fluid capability for fluid-handling machines
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                    BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getFluidStorage());
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                    BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(),
                    (be, side) -> be.getFluidStorage());
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(MenuRegistry.ATOMIZER_MENU.get(), AtomizerScreen::new);
            event.register(MenuRegistry.COMPACTOR_MENU.get(), CompactorScreen::new);
            event.register(MenuRegistry.COMBINER_MENU.get(), CombinerScreen::new);
            event.register(MenuRegistry.DISSOLVER_MENU.get(), DissolverScreen::new);
            event.register(MenuRegistry.LIQUIFIER_MENU.get(), LiquifierScreen::new);
            event.register(MenuRegistry.FISSION_CONTROLLER_MENU.get(), FissionControllerScreen::new);
            event.register(MenuRegistry.FUSION_CONTROLLER_MENU.get(), FusionControllerScreen::new);
        }

                @SubscribeEvent
                public static void onRecipesUpdated(RecipesUpdatedEvent event) {
                        RecipeRegistry.clearCaches();
                }
    }
    
    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}