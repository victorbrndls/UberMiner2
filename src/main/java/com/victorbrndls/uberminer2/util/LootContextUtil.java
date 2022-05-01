package com.victorbrndls.uberminer2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

public class LootContextUtil {

    @NotNull
    public static LootContext.Builder getLootContextBuilder(Level level, BlockPos pos) {
        return (new LootContext.Builder((ServerLevel) level))
                .withRandom(level.random)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE));
    }

}
