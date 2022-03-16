package al132.alchemistry.setup;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Registration;
import al132.alchemistry.network.Messages;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static al132.alchemistry.Alchemistry.MODID;

public class ModSetup {

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("Alchemistry") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.ATOMIZER_BLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent e) {
        Alchemistry.proxy.init();
        Messages.register();
        Registration.ATOMIZER_TYPE = RecipeType.register(MODID + ":atomizer");
        Registration.COMBINER_TYPE = RecipeType.register(MODID + ":combiner");
        Registration.DISSOLVER_TYPE = RecipeType.register(MODID + ":dissolver");
        Registration.EVAPORATOR_TYPE = RecipeType.register(MODID + ":evaporator");
        Registration.FISSION_TYPE = RecipeType.register(MODID + ":fission");
        Registration.LIQUIFIER_TYPE = RecipeType.register(MODID + ":liquifier");
    }
}