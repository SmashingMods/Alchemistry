package com.smashingmods.alchemistry;

import com.smashingmods.alchemistry.block.atomizer.AtomizerScreen;
import com.smashingmods.alchemistry.block.combiner.CombinerScreen;
import com.smashingmods.alchemistry.block.dissolver.DissolverScreen;
import com.smashingmods.alchemistry.block.evaporator.EvaporatorRenderer;
import com.smashingmods.alchemistry.block.evaporator.EvaporatorScreen;
import com.smashingmods.alchemistry.block.fission.FissionScreen;
import com.smashingmods.alchemistry.block.fusion.FusionScreen;
import com.smashingmods.alchemistry.block.liquifier.LiquifierScreen;
import com.smashingmods.alchemistry.network.Messages;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Alchemistry.MODID)
public class Alchemistry {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alchemistry";

    public Alchemistry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::clientSetupEvent);
        modEventBus.addListener(this::commonSetupEvent);
        Registry.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        MinecraftForge.EVENT_BUS.register(this);

        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("alchemistry-common.toml"));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::clientSetupEvent));

    }

    public void clientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registry.ATOMIZER_CONTAINER.get(), AtomizerScreen::new);
            MenuScreens.register(Registry.COMBINER_CONTAINER.get(), CombinerScreen::new);
            MenuScreens.register(Registry.DISSOLVER_CONTAINER.get(), DissolverScreen::new);
            MenuScreens.register(Registry.EVAPORATOR_CONTAINER.get(), EvaporatorScreen::new);
            MenuScreens.register(Registry.LIQUIFIER_CONTAINER.get(), LiquifierScreen::new);
            MenuScreens.register(Registry.FISSION_CONTAINER.get(), FissionScreen::new);
            MenuScreens.register(Registry.FUSION_CONTAINER.get(), FusionScreen::new);
            EvaporatorRenderer.register();
        });
    }

    public void commonSetupEvent(final FMLCommonSetupEvent event) {
        Messages.register();
        Registry.ATOMIZER_TYPE = RecipeType.register(Alchemistry.MODID + ":atomizer");
        Registry.COMBINER_TYPE = RecipeType.register(Alchemistry.MODID + ":combiner");
        Registry.DISSOLVER_TYPE = RecipeType.register(Alchemistry.MODID + ":dissolver");
        Registry.EVAPORATOR_TYPE = RecipeType.register(Alchemistry.MODID + ":evaporator");
        Registry.FISSION_TYPE = RecipeType.register(Alchemistry.MODID + ":fission");
        Registry.LIQUIFIER_TYPE = RecipeType.register(Alchemistry.MODID + ":liquifier");
    }
}