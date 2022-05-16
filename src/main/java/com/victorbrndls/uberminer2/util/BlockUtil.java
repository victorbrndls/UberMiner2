package com.victorbrndls.uberminer2.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtil {

    private static final ResourceLocation oreResourceLocation = new ResourceLocation("forge", "ores");

    public static boolean isOre(BlockState blockState) {
        return blockState.getBlock().getTags().stream().anyMatch((tag) -> tag.equals(oreResourceLocation));
    }

    public static boolean isResource(BlockState blockState) {
        if (blockState.isAir()) return false;
        if (blockState.hasBlockEntity()) return false;
        if (blockState.getBlock().defaultDestroyTime() == -1f) return false;

        return blockState.getTags().noneMatch((tag) -> tag.location().equals(oreResourceLocation));
    }

}
