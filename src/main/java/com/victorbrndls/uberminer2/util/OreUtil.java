package com.victorbrndls.uberminer2.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class OreUtil {

    private static final ResourceLocation oreResourceLocation = new ResourceLocation("forge", "ores");

    public static boolean isOre(BlockState blockState) {
        return blockState.getTags().anyMatch((tag) -> tag.location().equals(oreResourceLocation));
    }

}
