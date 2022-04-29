package com.victorbrndls.uberminer2.render;

import com.victorbrndls.uberminer2.registry.UberMinerEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class UberMinerRenders {

    public static void init(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(UberMinerEntities.UBER_BALL.get(), ThrownItemRenderer::new);
    }

}
