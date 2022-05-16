package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.gui.menu.ResourceMinerContainer;
import com.victorbrndls.uberminer2.gui.menu.UberMinerContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UberMinerMenus {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS,
                                                                                       UberMiner.MOD_ID);

    public static final RegistryObject<MenuType<UberMinerContainer>> UBER_MINER =
            MENUS.register("uber_miner", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos blockPos = data.readBlockPos();
                Level level = inv.player.getCommandSenderWorld();
                return new UberMinerContainer(windowId, level, blockPos, inv, inv.player);
            }));

    public static final RegistryObject<MenuType<ResourceMinerContainer>> RESOURCE_MINER =
            MENUS.register("resource_miner", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos blockPos = data.readBlockPos();
                Level level = inv.player.getCommandSenderWorld();
                return new ResourceMinerContainer(windowId, level, blockPos, inv, inv.player);
            }));

    public static void init() {
        MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
