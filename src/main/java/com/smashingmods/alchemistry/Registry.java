package com.smashingmods.alchemistry;


import com.smashingmods.alchemistry.blocks.atomizer.*;
import com.smashingmods.alchemistry.blocks.combiner.*;
import com.smashingmods.alchemistry.blocks.dissolver.*;
import com.smashingmods.alchemistry.blocks.evaporator.*;
import com.smashingmods.alchemistry.blocks.fission.*;
import com.smashingmods.alchemistry.blocks.fusion.*;
import com.smashingmods.alchemistry.blocks.liquifier.*;
import com.smashingmods.alchemistry.items.CondensedMilkItem;
import com.smashingmods.alchemistry.items.MineralSaltItem;
import com.smashingmods.alchemistry.items.SlotFillerItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

import javax.annotation.Nonnull;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Registry {

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

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("Alchemistry") {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(Registry.ATOMIZER_BLOCK.get());
        }
    };

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ITEM_GROUP);

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

    public static final RegistryObject<BlockEntityType<AtomizerBlockEntity>> ATOMIZER_BE
            = BLOCK_ENTITIES.register("atomizer",
            () -> BlockEntityType.Builder.of(AtomizerBlockEntity::new, ATOMIZER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CombinerBlockEntity>> COMBINER_BE
            = BLOCK_ENTITIES.register("combiner",
            () -> BlockEntityType.Builder.of(CombinerBlockEntity::new, COMBINER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<DissolverBlockEntity>> DISSOLVER_BE
            = BLOCK_ENTITIES.register("dissolver",
            () -> BlockEntityType.Builder.of(DissolverBlockEntity::new, DISSOLVER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<EvaporatorBlockEntity>> EVAPORATOR_BE
            = BLOCK_ENTITIES.register("evaporator",
            () -> BlockEntityType.Builder.of(EvaporatorBlockEntity::new, EVAPORATOR_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<LiquifierBlockEntity>> LIQUIFIER_BE
            = BLOCK_ENTITIES.register("liquifier",
            () -> BlockEntityType.Builder.of(LiquifierBlockEntity::new, LIQUIFIER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FissionBlockEntity>> FISSION_CONTOLLER_BE
            = BLOCK_ENTITIES.register("fission_controller",
            () -> BlockEntityType.Builder.of(FissionBlockEntity::new, FISSION_CONTROLLER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FusionBlockEntity>> FUSION_CONTROLLER_BE
            = BLOCK_ENTITIES.register("fusion_controller",
            () -> BlockEntityType.Builder.of(FusionBlockEntity::new, FUSION_CONTROLLER_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<AtomizerContainer>> ATOMIZER_CONTAINER = CONTAINERS.register("atomizer",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new AtomizerContainer(windowId, data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<CombinerContainer>> COMBINER_CONTAINER = CONTAINERS.register("combiner",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new CombinerContainer(windowId, data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<DissolverContainer>> DISSOLVER_CONTAINER = CONTAINERS.register("dissolver",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new DissolverContainer(windowId, data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<EvaporatorContainer>> EVAPORATOR_CONTAINER = CONTAINERS.register("evaporator",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new EvaporatorContainer(windowId, data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<LiquifierContainer>> LIQUIFIER_CONTAINER = CONTAINERS.register("liquifier",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new LiquifierContainer(windowId, data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<FissionContainer>> FISSION_CONTAINER = CONTAINERS.register("fission",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new FissionContainer(windowId, data.readBlockPos(), inv)));
    public static final RegistryObject<MenuType<FusionContainer>> FUSION_CONTAINER = CONTAINERS.register("fusion",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new FusionContainer(windowId, data.readBlockPos(), inv)));

    public static final RegistryObject<AtomizerRecipeSerializer<AtomizerRecipe>> ATOMIZER_SERIALIZER
            = RECIPE_SERIALIZERS.register("atomizer", () -> new AtomizerRecipeSerializer<>(AtomizerRecipe::new));
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
