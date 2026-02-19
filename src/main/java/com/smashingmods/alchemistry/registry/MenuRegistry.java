package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerMenu;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.block.compactor.CompactorMenu;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerMenu;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerMenu;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AtomizerMenu>> ATOMIZER_MENU = registerMenuType(AtomizerMenu::new, "atomizer_menu");
    public static final DeferredHolder<MenuType<?>, MenuType<CompactorMenu>> COMPACTOR_MENU = registerMenuType(CompactorMenu::new, "compactor_menu");
    public static final DeferredHolder<MenuType<?>, MenuType<CombinerMenu>> COMBINER_MENU = registerMenuType(CombinerMenu::new, "combiner_menu");
    public static final DeferredHolder<MenuType<?>, MenuType<DissolverMenu>> DISSOLVER_MENU = registerMenuType(DissolverMenu::new, "dissolver_menu");
    public static final DeferredHolder<MenuType<?>, MenuType<LiquifierMenu>> LIQUIFIER_MENU = registerMenuType(LiquifierMenu::new, "liquifier_menu");
    public static final DeferredHolder<MenuType<?>, MenuType<FissionControllerMenu>> FISSION_CONTROLLER_MENU = registerMenuType(FissionControllerMenu::new, "fission_controller_menu");
    public static final DeferredHolder<MenuType<?>, MenuType<FusionControllerMenu>> FUSION_CONTROLLER_MENU = registerMenuType(FusionControllerMenu::new, "fusion_controller_menu");

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENU_TYPES.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
