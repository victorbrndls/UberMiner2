package com.victorbrndls.uberminer2;

import com.victorbrndls.uberminer2.entity.UberMinerEntities;
import com.victorbrndls.uberminer2.item.UberMinerItems;
import com.victorbrndls.uberminer2.render.UberMinerRenders;
import com.victorbrndls.uberminer2.tab.UberMinerTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(UberMiner.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = UberMiner.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UberMiner {

    public static final String MOD_ID = "uberminer2";
    public static final CreativeModeTab UBER_MINER_TAB = new UberMinerTab();

    public UberMiner() {
        UberMinerItems.init();
        UberMinerEntities.init();
    }

    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        UberMinerRenders.init(event);
    }
}
