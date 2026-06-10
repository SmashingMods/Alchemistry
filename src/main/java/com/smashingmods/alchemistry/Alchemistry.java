package com.smashingmods.alchemistry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerScreen;
import com.smashingmods.alchemistry.common.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.common.block.compactor.CompactorScreen;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverScreen;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerScreen;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerScreen;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierScreen;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemistry.registry.RecipeSyncHandler;
import com.smashingmods.alchemistry.registry.Registry;
import com.smashingmods.alchemylib.api.capability.AlchemyCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Alchemistry.MODID)
public class Alchemistry {

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alchemistry";
    // Mirrors ChemLib.MOD_ID_TEXT_STYLE so both mods' tooltip footers render identically.
    public static final Style MOD_ID_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withItalic(true).withColor(ChatFormatting.BLUE);
    public static final PacketHandler PACKET_HANDLER = new PacketHandler();

    public Alchemistry(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(PACKET_HANDLER::register);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Registry.register(modEventBus);

        // Make sure that `/reload` and world loading wipe the machine recipe cache.
        NeoForge.EVENT_BUS.addListener(RecipeRegistry::postReload);

        // The machine recipe list is server-only, so push it to clients on join and on `/reload`
        // for the recipe-selector GUI.
        NeoForge.EVENT_BUS.addListener(RecipeSyncHandler::onDatapackSync);
    }

    /**
     * Exposes the block-entity capabilities for Alchemistry's machines and reactor ports. AlchemyLib's block-entity
     * bases are abstract and don't own the concrete {@link net.minecraft.world.level.block.entity.BlockEntityType}s,
     * so they're registered here -- keyed on Alchemistry's own types -- through {@link AlchemyCapabilities}'s helpers.
     * The single-block machines expose what their AlchemyLib base did (energy + items, plus a fluid handler for the
     * {@code AbstractFluidBlockEntity}-derived atomizer/liquifier). The three reactor ports own no handlers of their
     * own; each proxies a single capability to its controller, so they're wired with the primitive helper and a
     * resolver that defers to the adopted controller and returns {@code null} until one is set.
     */
    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        AlchemyCapabilities.registerInventory(event, BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get());
        AlchemyCapabilities.registerInventory(event, BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get());
        AlchemyCapabilities.registerInventory(event, BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get());
        AlchemyCapabilities.registerFluid(event, BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get());
        AlchemyCapabilities.registerFluid(event, BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get());

        AlchemyCapabilities.registerInventory(event, BlockEntityRegistry.FISSION_CONTROLLER_BLOCK_ENTITY.get());
        AlchemyCapabilities.registerInventory(event, BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get());

        AlchemyCapabilities.register(event, Capabilities.EnergyStorage.BLOCK, BlockEntityRegistry.REACTOR_ENERGY_BLOCK_ENTITY.get(),
                (be, side) -> be.getController() != null ? be.getController().getEnergyHandler() : null);
        AlchemyCapabilities.register(event, Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.REACTOR_INPUT_BLOCK_ENTITY.get(),
                (be, side) -> be.getController() != null ? be.getController().getInputHandler() : null);
        AlchemyCapabilities.register(event, Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.REACTOR_OUTPUT_BLOCK_ENTITY.get(),
                (be, side) -> be.getController() != null ? be.getController().getOutputHandler() : null);
    }

    public void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.ATOMIZER_MENU.get(), AtomizerScreen::new);
        event.register(MenuRegistry.COMPACTOR_MENU.get(), CompactorScreen::new);
        event.register(MenuRegistry.COMBINER_MENU.get(), CombinerScreen::new);
        event.register(MenuRegistry.DISSOLVER_MENU.get(), DissolverScreen::new);
        event.register(MenuRegistry.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(MenuRegistry.FISSION_CONTROLLER_MENU.get(), FissionControllerScreen::new);
        event.register(MenuRegistry.FUSION_CONTROLLER_MENU.get(), FusionControllerScreen::new);
    }
}