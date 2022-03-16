package al132.alchemistry.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();

        if (e.includeServer()) {
            gen.addProvider(new Recipes(gen));
        }
        /*
        ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;
        ByteBuf bb = alloc.directBuffer(1024);
        FriendlyByteBuf buf = new FriendlyByteBuf(bb);
        CombinerRecipe x = new CombinerRecipe(new ResourceLocation("x:y"), "minecraft:misc",
                Lists.newArrayList(new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.APPLE)),
                new ItemStack(Items.GOLDEN_APPLE));
        ((CombinerRecipeSerializer) x.getSerializer()).write(buf, x);
        CombinerRecipe y = ((CombinerRecipeSerializer) x.getSerializer()).read(new ResourceLocation("x:y"), buf);
        System.out.println(x);
        System.out.println(y);

        ProbabilityGroup group = new ProbabilityGroup(Lists.newArrayList(new ItemStack(Items.APPLE)), 1.0);
        ProbabilitySet set = new ProbabilitySet(Lists.newArrayList(group));
        DissolverRecipe x = new DissolverRecipe(new ResourceLocation("x:y"), "minecraft:misc",
                new IngredientStack(Ingredient.fromItems(Items.BEETROOT), 3), set);

        ((DissolverRecipeSerializer)x.getSerializer()).write(buf, x);
        DissolverRecipe y = ((DissolverRecipeSerializer)x.getSerializer()).read(new ResourceLocation("x:y"), buf);
        System.out.println(x);
        System.out.println(y);

         */
    }
}

