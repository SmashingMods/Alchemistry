package al132.alchemistry;

import al132.alchemistry.blocks.atomizer.*;
import al132.alchemistry.blocks.combiner.*;
import al132.alchemistry.blocks.dissolver.*;
import al132.alchemistry.blocks.evaporator.*;
import al132.alchemistry.blocks.fission.*;
import al132.alchemistry.blocks.fusion.*;
import al132.alchemistry.blocks.liquifier.*;
import al132.alchemistry.items.CondensedMilkItem;
import al132.alchemistry.items.MineralSaltItem;
import al132.alchemistry.items.SlotFillerItem;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;

import java.text.DecimalFormat;

public class  Ref {

    public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public static CombinerBlock combiner;
    public static DissolverBlock dissolver;
    public static EvaporatorBlock evaporator;
    public static AtomizerBlock atomizer;
    public static LiquifierBlock liquifier;
    public static FissionControllerBlock fissionController;
    public static FissionCasingBlock fissionCasing;
    public static FissionCoreBlock fissionCore;
    public static FusionControllerBlock fusionController;
    public static FusionCasingBlock fusionCasing;
    public static FusionCoreBlock fusionCore;

    public static SlotFillerItem slotFiller;
    public static MineralSaltItem mineralSalt;
    public static CondensedMilkItem condensedMilk;

    public static ContainerType<CombinerContainer> combinerContainer;
    public static ContainerType<DissolverContainer> dissolverContainer;
    public static ContainerType<EvaporatorContainer> evaporatorContainer;
    public static ContainerType<AtomizerContainer> atomizerContainer;
    public static ContainerType<LiquifierContainer> liquifierContainer;
    public static ContainerType<FissionContainer> fissionContainer;
    public static ContainerType<FusionContainer> fusionContainer;

    public static TileEntityType<CombinerTile> combinerTile;
    public static TileEntityType<DissolverTile> dissolverTile;
    public static TileEntityType<EvaporatorTile> evaporatorTile;
    public static TileEntityType<AtomizerTile> atomizerTile;
    public static TileEntityType<LiquifierTile> liquifierTile;
    public static TileEntityType<FissionTile> fissionTile;
    public static TileEntityType<FusionTile> fusionTile;

    public static AtomizerRecipeSerializer<AtomizerRecipe> ATOMIZER_SERIALIZER = new AtomizerRecipeSerializer<>(AtomizerRecipe::new);
    public static CombinerRecipeSerializer<CombinerRecipe> COMBINER_SERIALIZER = new CombinerRecipeSerializer<>(CombinerRecipe::new);
    public static DissolverRecipeSerializer<DissolverRecipe> DISSOLVER_SERIALIZER = new DissolverRecipeSerializer<>(DissolverRecipe::new);
    public static EvaporatorRecipeSerializer<EvaporatorRecipe> EVAPORATOR_SERIALIZER = new EvaporatorRecipeSerializer<>(EvaporatorRecipe::new);
    public static FissionRecipeSerializer<FissionRecipe> FISSION_SERIALIZER = new FissionRecipeSerializer<>(FissionRecipe::new);
    public static LiquifierRecipeSerializer<LiquifierRecipe> LIQUIFIER_SERIALIZER = new LiquifierRecipeSerializer<>(LiquifierRecipe::new);

    public static void initBlocks() {
        combiner = new CombinerBlock();
        dissolver = new DissolverBlock();
        evaporator = new EvaporatorBlock();
        atomizer = new AtomizerBlock();
        liquifier = new LiquifierBlock();
        fissionCasing = new FissionCasingBlock();
        fissionController = new FissionControllerBlock();
        fissionCore = new FissionCoreBlock();
        fusionCasing = new FusionCasingBlock();
        fusionController = new FusionControllerBlock();
        fusionCore = new FusionCoreBlock();
    }

    public static void initItems() {
        slotFiller = new SlotFillerItem();
        mineralSalt = new MineralSaltItem();
        condensedMilk = new CondensedMilkItem();
    }
}
