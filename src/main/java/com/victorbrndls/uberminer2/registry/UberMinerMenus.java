package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.gui.menu.UberMinerMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UberMinerMenus {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS,
            UberMiner.MOD_ID);

    public static final RegistryObject<MenuType<UberMinerMenu>> UBER_MINER = MENUS.register("uber_miner",
            () -> new MenuType<>(UberMinerMenu::new));

    public static void init() {
        MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
