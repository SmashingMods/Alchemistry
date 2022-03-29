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
import al132.alchemistry.setup.ModSetup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static al132.alchemistry.Alchemistry.MODID;

public class Registration {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static RecipeType<AtomizerRecipe> ATOMIZER_TYPE;// = register(MODID + ":atomizer");
    public static RecipeType<CombinerRecipe> COMBINER_TYPE;// = register(MODID + ":combiner");
    public static RecipeType<DissolverRecipe> DISSOLVER_TYPE;// = register(MODID + ":dissolver");
    public static RecipeType<EvaporatorRecipe> EVAPORATOR_TYPE;// = register(MODID + ":evaporator");
    public static RecipeType<FissionRecipe> FISSION_TYPE;// = register(MODID + ":fission");
    public static RecipeType<LiquifierRecipe> LIQUIFIER_TYPE;// = register(MODID + ":liquifier");


    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
        RECIPE_SERIALIZERS.register(bus);

    }

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModSetup.ITEM_GROUP);


    public static final RegistryObject<Block> ATOMIZER_BLOCK = BLOCKS.register("atomizer", AtomizerBlock::new);
    public static final RegistryObject<Block> COMBINER_BLOCK = BLOCKS.register("combiner", CombinerBlock::new);
    public static final RegistryObject<Block> DISSOLVER_BLOCK = BLOCKS.register("dissolver", DissolverBlock::new);
    public static final RegistryObject<Block> EVAPORATOR_BLOCK = BLOCKS.register("evaporator", EvaporatorBlock::new);
    public static final RegistryObject<Block> LIQUIFIER_BLOCK = BLOCKS.register("liquifier", LiquifierBlock::new);
    public static final RegistryObject<Block> FISSION_CONTROLLER_BLOCK = BLOCKS.register("fission_controller", FissionControllerBlock::new);
    public static final RegistryObject<Block> FISSION_CASING_BLOCK = BLOCKS.register("fission_casing", FissionCasingBlock::new);
    public static final RegistryObject<Block> FISSION_CORE_BLOCK = BLOCKS.register("fission_core", FissionCoreBlock::new);
    public static final RegistryObject<Block> FUSION_CONTROLLER_BLOCK = BLOCKS.register("fusion_controller", FusionControllerBlock::new);
    public static final RegistryObject<Block> FUSION_CASING_BLOCK = BLOCKS.register("fusion_casing", FusionCasingBlock::new);
    public static final RegistryObject<Block> FUSION_CORE_BLOCK = BLOCKS.register("fusion_core", FusionCoreBlock::new);

    public static final RegistryObject<Item> ATOMIZER_ITEM = fromBlock(ATOMIZER_BLOCK);
    public static final RegistryObject<Item> COMBINER_ITEM = fromBlock(COMBINER_BLOCK);
    public static final RegistryObject<Item> DISSOLVER_ITEM = fromBlock(DISSOLVER_BLOCK);
    public static final RegistryObject<Item> EVAPORATOR_ITEM = fromBlock(EVAPORATOR_BLOCK);
    public static final RegistryObject<Item> LIQUIFIER_ITEM = fromBlock(LIQUIFIER_BLOCK);
    public static final RegistryObject<Item> FISSION_CONTROLLER_ITEM = fromBlock(FISSION_CONTROLLER_BLOCK);
    public static final RegistryObject<Item> FISSION_CASING_ITEM = fromBlock(FISSION_CASING_BLOCK);
    public static final RegistryObject<Item> FISSION_CORE_ITEM = fromBlock(FISSION_CORE_BLOCK);
    public static final RegistryObject<Item> FUSION_CONTROLLER_ITEM = fromBlock(FUSION_CONTROLLER_BLOCK);
    public static final RegistryObject<Item> FUSION_CASING_ITEM = fromBlock(FUSION_CASING_BLOCK);
    public static final RegistryObject<Item> FUSION_CORE_ITEM = fromBlock(FUSION_CORE_BLOCK);
    public static final RegistryObject<Item> SLOT_FILLER_ITEM = ITEMS.register("slot_filler", SlotFillerItem::new);
    public static final RegistryObject<Item> MINERAL_SALT_ITEM = ITEMS.register("mineral_salt", MineralSaltItem::new);
    public static final RegistryObject<Item> CONDENSED_MILK_ITEM = ITEMS.register("condensed_milk", CondensedMilkItem::new);

    public static final RegistryObject<BlockEntityType<AtomizerTile>> ATOMIZER_BE
            = BLOCK_ENTITIES.register("atomizer",
            () -> BlockEntityType.Builder.of(AtomizerTile::new, ATOMIZER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CombinerTile>> COMBINER_BE
            = BLOCK_ENTITIES.register("combiner",
            () -> BlockEntityType.Builder.of(CombinerTile::new, COMBINER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<DissolverTile>> DISSOLVER_BE
            = BLOCK_ENTITIES.register("dissolver",
            () -> BlockEntityType.Builder.of(DissolverTile::new, DISSOLVER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<EvaporatorTile>> EVAPORATOR_BE
            = BLOCK_ENTITIES.register("evaporator",
            () -> BlockEntityType.Builder.of(EvaporatorTile::new, EVAPORATOR_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<LiquifierTile>> LIQUIFIER_BE
            = BLOCK_ENTITIES.register("liquifier",
            () -> BlockEntityType.Builder.of(LiquifierTile::new, LIQUIFIER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FissionTile>> FISSION_CONTOLLER_BE
            = BLOCK_ENTITIES.register("fission_controller",
            () -> BlockEntityType.Builder.of(FissionTile::new, FISSION_CONTROLLER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FusionTile>> FUSION_CONTROLLER_BE
            = BLOCK_ENTITIES.register("fusion_controller",
            () -> BlockEntityType.Builder.of(FusionTile::new, FUSION_CONTROLLER_BLOCK.get()).build(null));


    public static final RegistryObject<MenuType<AtomizerContainer>> ATOMIZER_CONTAINER = CONTAINERS.register("atomizer",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new AtomizerContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<CombinerContainer>> COMBINER_CONTAINER = CONTAINERS.register("combiner",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new CombinerContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<DissolverContainer>> DISSOLVER_CONTAINER = CONTAINERS.register("dissolver",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new DissolverContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<EvaporatorContainer>> EVAPORATOR_CONTAINER = CONTAINERS.register("evaporator",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new EvaporatorContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<LiquifierContainer>> LIQUIFIER_CONTAINER = CONTAINERS.register("liquifier",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new LiquifierContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<FissionContainer>> FISSION_CONTAINER = CONTAINERS.register("fission",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new FissionContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<FusionContainer>> FUSION_CONTAINER = CONTAINERS.register("fusion",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new FusionContainer(windowId, Alchemistry.proxy.getClientWorld(), data.readBlockPos(), inv)));

    public static final RegistryObject<AtomizerRecipeSerializer<AtomizerRecipe>> ATOMIZER_SERIALIZER
            = RECIPE_SERIALIZERS.register("atomizer", () -> new AtomizerRecipeSerializer(AtomizerRecipe::new));
    public static final RegistryObject<CombinerRecipeSerializer<CombinerRecipe>> COMBINER_SERIALIZER
            = RECIPE_SERIALIZERS.register("combiner", () -> new CombinerRecipeSerializer<>(CombinerRecipe::new));
    public static final RegistryObject<DissolverRecipeSerializer<DissolverRecipe>> DISSOLVER_SERIALIZER
            = RECIPE_SERIALIZERS.register("dissolver", () -> new DissolverRecipeSerializer<>(DissolverRecipe::new));
    public static final RegistryObject<EvaporatorRecipeSerializer<EvaporatorRecipe>> EVAPORATOR_SERIALIZER
            = RECIPE_SERIALIZERS.register("evaporator", () -> new EvaporatorRecipeSerializer<>(EvaporatorRecipe::new));
    public static final RegistryObject<FissionRecipeSerializer<FissionRecipe>> FISSION_SERIALIZER
            = RECIPE_SERIALIZERS.register("fission", () -> new FissionRecipeSerializer<>(FissionRecipe::new));
    public static final RegistryObject<LiquifierRecipeSerializer<LiquifierRecipe>> LIQUIFIER_SERIALIZER
            = RECIPE_SERIALIZERS.register("liquifier", () -> new LiquifierRecipeSerializer<>(LiquifierRecipe::new));


    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
