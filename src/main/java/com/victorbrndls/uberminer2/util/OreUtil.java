package com.victorbrndls.uberminer2.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class OreUtil {

    public static boolean isOre(BlockState blockState) {
        var oreResourceLocation = new ResourceLocation("forge", "ores");
        return blockState.getTags().anyMatch((tag) -> tag.location().equals(oreResourceLocation));
    }

}
