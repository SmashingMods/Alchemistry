package al132.alchemistry;

import al132.alchemistry.network.NetworkHandler;
import al132.alchemistry.setup.ClientProxy;
import al132.alchemistry.setup.IProxy;
import al132.alchemistry.setup.ServerProxy;
import al132.alchemistry.utils.IngredientStack;
import al132.alib.ModData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("alchemistry")
public class Alchemistry {

    public static final Logger LOGGER = LogManager.getLogger();

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static ModData data = new AlchemistryData();

    public static final String MODID = "alchemistry";


    public Alchemistry() {
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);

        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("alchemistry-common.toml"));
    }


    private void setup(final FMLCommonSetupEvent e) {
        proxy.init();
        DeferredWorkQueue.runLater(() -> {
            NetworkHandler.register();
        });
        //ModRecipes.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onTilesRegistry(final RegistryEvent.Register<TileEntityType<?>> e) {
            data.registerTiles(e);
        }

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> e) {
            Ref.initBlocks();
            data.BLOCKS.forEach(e.getRegistry()::register);
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
            data.registerContainers(e);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> e) {
            Ref.initItems();
            data.ITEMS.forEach(e.getRegistry()::register);
            data.BLOCKS.forEach(x -> e.getRegistry()
                    .register(new BlockItem(x, new Item.Properties().group(data.itemGroup)).setRegistryName(x.getRegistryName())));
        }

        @SubscribeEvent
        public static void onRecipeSerializerRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> e) {
            e.getRegistry().register(Ref.ATOMIZER_SERIALIZER.setRegistryName(MODID, "atomizer"));
            e.getRegistry().register(Ref.COMBINER_SERIALIZER.setRegistryName(MODID, "combiner"));
            e.getRegistry().register(Ref.DISSOLVER_SERIALIZER.setRegistryName(MODID, "dissolver"));
            e.getRegistry().register(Ref.EVAPORATOR_SERIALIZER.setRegistryName(MODID, "evaporator"));
            e.getRegistry().register(Ref.FISSION_SERIALIZER.setRegistryName(MODID, "fission"));
            e.getRegistry().register(Ref.LIQUIFIER_SERIALIZER.setRegistryName(MODID, "liquifier"));
        }
    }
}
