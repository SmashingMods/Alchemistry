package al132.alchemistry;

import al132.alchemistry.blocks.atomizer.AtomizerContainer;
import al132.alchemistry.blocks.atomizer.AtomizerTile;
import al132.alchemistry.blocks.combiner.CombinerContainer;
import al132.alchemistry.blocks.combiner.CombinerTile;
import al132.alchemistry.blocks.dissolver.DissolverContainer;
import al132.alchemistry.blocks.dissolver.DissolverTile;
import al132.alchemistry.blocks.evaporator.EvaporatorContainer;
import al132.alchemistry.blocks.evaporator.EvaporatorTile;
import al132.alchemistry.blocks.fission.FissionContainer;
import al132.alchemistry.blocks.fission.FissionTile;
import al132.alchemistry.blocks.fusion.FusionContainer;
import al132.alchemistry.blocks.fusion.FusionTile;
import al132.alchemistry.blocks.liquifier.LiquifierContainer;
import al132.alchemistry.blocks.liquifier.LiquifierTile;
import al132.alib.ModData;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;

import static al132.alchemistry.Alchemistry.proxy;

public class AlchemistryData extends ModData {

    public AlchemistryData() {
        super("alchemistry", new ItemStack(Blocks.DIRT));
    }

    @Override
    public void registerTiles(RegistryEvent.Register<TileEntityType<?>> e) {
        Ref.combinerTile = Alchemistry.data.registerTile(CombinerTile::new, Ref.combiner, "chemical_combiner");
        Ref.dissolverTile = Alchemistry.data.registerTile(DissolverTile::new, Ref.dissolver, "chemical_dissolver");
        Ref.evaporatorTile = Alchemistry.data.registerTile(EvaporatorTile::new, Ref.evaporator, "evaporator");
        Ref.atomizerTile = Alchemistry.data.registerTile(AtomizerTile::new, Ref.atomizer, "atomizer");
        Ref.liquifierTile = Alchemistry.data.registerTile(LiquifierTile::new, Ref.liquifier, "liquifier");
        Ref.fissionTile = Alchemistry.data.registerTile(FissionTile::new, Ref.fissionController, "fission");
        Ref.fusionTile = Alchemistry.data.registerTile(FusionTile::new, Ref.fusionController, "fusion");
        Alchemistry.data.TILES.forEach(e.getRegistry()::register);
    }

    @Override
    public void registerContainers(RegistryEvent.Register<ContainerType<?>> e) {
        Ref.combinerContainer = IForgeContainerType.create((windowID, inv, data) ->
                new CombinerContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.combinerContainer, "chemical_combiner_container");

        Ref.dissolverContainer = IForgeContainerType.create((windowID, inv, data) ->
                new DissolverContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.dissolverContainer, "chemical_dissolver_container");

        Ref.evaporatorContainer = IForgeContainerType.create((windowID, inv, data) ->
                new EvaporatorContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.evaporatorContainer, "evaporator_container");

        Ref.atomizerContainer = IForgeContainerType.create((windowID, inv, data) ->
                new AtomizerContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.atomizerContainer, "atomizer_container");

        Ref.liquifierContainer = IForgeContainerType.create((windowID, inv, data) ->
                new LiquifierContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.liquifierContainer, "liquifier_container");

        Ref.fissionContainer = IForgeContainerType.create((windowID, inv, data) ->
                new FissionContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.fissionContainer, "fission_container");

        Ref.fusionContainer = IForgeContainerType.create((windowID, inv, data) ->
                new FusionContainer(windowID, proxy.getClientWorld(), data.readBlockPos(), inv, proxy.getClientPlayer()));
        Alchemistry.data.registerContainer(Ref.fusionContainer, "fusion_container");

        Alchemistry.data.CONTAINERS.forEach(e.getRegistry()::register);
    }
}