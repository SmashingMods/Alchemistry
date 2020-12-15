package al132.alchemistry.datagen;

import al132.alchemistry.blocks.combiner.CombinerRecipe;
import al132.alchemistry.blocks.combiner.CombinerRecipeSerializer;
import al132.alchemistry.blocks.dissolver.DissolverRecipe;
import al132.alchemistry.blocks.dissolver.DissolverRecipeSerializer;
import al132.alchemistry.misc.ProbabilityGroup;
import al132.alchemistry.misc.ProbabilitySet;
import al132.alchemistry.utils.IngredientStack;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

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
        PacketBuffer buf = new PacketBuffer(bb);
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

