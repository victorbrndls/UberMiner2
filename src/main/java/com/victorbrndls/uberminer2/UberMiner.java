package com.victorbrndls.uberminer2;

import com.victorbrndls.uberminer2.gui.screen.ResourceMinerScreen;
import com.victorbrndls.uberminer2.gui.screen.UberMinerScreen;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;
import com.victorbrndls.uberminer2.registry.UberMinerBlocks;
import com.victorbrndls.uberminer2.registry.UberMinerEntities;
import com.victorbrndls.uberminer2.registry.UberMinerItems;
import com.victorbrndls.uberminer2.registry.UberMinerMenus;
import com.victorbrndls.uberminer2.registry.UberMinerRecipes;
import com.victorbrndls.uberminer2.render.UberMinerRenders;
import com.victorbrndls.uberminer2.tab.UberMinerTab;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(UberMiner.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = UberMiner.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UberMiner {

    public static final String MOD_ID = "uberminer2";
    public static final Logger LOGGER = LogManager.getLogger("UberMiner2");
    public static final CreativeModeTab UBER_MINER_TAB = new UberMinerTab();

    public UberMiner() {
        UberMinerItems.init();
        UberMinerBlocks.init();
        UberMinerEntities.init();
        UberMinerBlockEntities.init();
        UberMinerMenus.init();
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        MenuScreens.register(UberMinerMenus.UBER_MINER.get(), UberMinerScreen::new);
        MenuScreens.register(UberMinerMenus.RESOURCE_MINER.get(), ResourceMinerScreen::new);
    }

    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        UberMinerRenders.init(event);
    }

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        UberMinerRecipes.init(event.getRegistry());
    }
}
