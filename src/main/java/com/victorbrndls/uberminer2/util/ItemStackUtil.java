package com.victorbrndls.uberminer2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemStackUtil {

    public static void drop(Level level, BlockPos position, ItemStack itemStack) {
        var itemEntity = new ItemEntity(level, position.getX(), position.getY(), position.getZ(), itemStack);
        itemEntity.setPickUpDelay(5);
        level.addFreshEntity(itemEntity);
    }

    public static void drop(Level level, BlockPos position, List<ItemStack> items) {
        items.forEach(item -> drop(level, position, item));
    }

}
