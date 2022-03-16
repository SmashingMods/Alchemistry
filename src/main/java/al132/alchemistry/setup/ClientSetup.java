package al132.alchemistry.setup;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Registration;
import al132.alchemistry.blocks.atomizer.AtomizerScreen;
import al132.alchemistry.blocks.combiner.CombinerScreen;
import al132.alchemistry.blocks.dissolver.DissolverScreen;
import al132.alchemistry.blocks.evaporator.EvaporatorRenderer;
import al132.alchemistry.blocks.evaporator.EvaporatorScreen;
import al132.alchemistry.blocks.fission.FissionScreen;
import al132.alchemistry.blocks.fusion.FusionScreen;
import al132.alchemistry.blocks.liquifier.LiquifierScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Alchemistry.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            MenuScreens.register(Registration.ATOMIZER_CONTAINER.get(), AtomizerScreen::new);
            MenuScreens.register(Registration.COMBINER_CONTAINER.get(), CombinerScreen::new);
            MenuScreens.register(Registration.DISSOLVER_CONTAINER.get(), DissolverScreen::new);
            MenuScreens.register(Registration.EVAPORATOR_CONTAINER.get(), EvaporatorScreen::new);
            MenuScreens.register(Registration.LIQUIFIER_CONTAINER.get(), LiquifierScreen::new);
            MenuScreens.register(Registration.FISSION_CONTAINER.get(), FissionScreen::new);
            MenuScreens.register(Registration.FUSION_CONTAINER.get(), FusionScreen::new);
            EvaporatorRenderer.register();
        });
    }
}