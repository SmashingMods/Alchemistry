package al132.alchemistry.setup;

import al132.alchemistry.Ref;
import al132.alchemistry.blocks.atomizer.AtomizerScreen;
import al132.alchemistry.blocks.combiner.CombinerScreen;
import al132.alchemistry.blocks.dissolver.DissolverScreen;
import al132.alchemistry.blocks.evaporator.EvaporatorScreen;
import al132.alchemistry.blocks.evaporator.EvaporatorTESR;
import al132.alchemistry.blocks.evaporator.EvaporatorTile;
import al132.alchemistry.blocks.fission.FissionScreen;
import al132.alchemistry.blocks.fusion.FusionScreen;
import al132.alchemistry.blocks.liquifier.LiquifierScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {
    @Override
    public void init() {
        ScreenManager.registerFactory(Ref.combinerContainer, CombinerScreen::new);
        ScreenManager.registerFactory(Ref.dissolverContainer, DissolverScreen::new);
        ScreenManager.registerFactory(Ref.evaporatorContainer, EvaporatorScreen::new);
        ScreenManager.registerFactory(Ref.atomizerContainer, AtomizerScreen::new);
        ScreenManager.registerFactory(Ref.liquifierContainer, LiquifierScreen::new);
        ScreenManager.registerFactory(Ref.fissionContainer, FissionScreen::new);
        ScreenManager.registerFactory(Ref.fusionContainer, FusionScreen::new);
        ClientRegistry.bindTileEntitySpecialRenderer(EvaporatorTile.class, new EvaporatorTESR());
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
