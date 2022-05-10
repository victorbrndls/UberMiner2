package com.victorbrndls.uberminer2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

public class InventoryUtil {

    public static List<ItemStack> tryMoveInItems(IItemHandler itemHandler, List<ItemStack> inStacks) {
        if (isItemHandlerFull(itemHandler)) return inStacks;

        List<ItemStack> notMoved = new ArrayList<>();
        final var itemHandlerSlots = itemHandler.getSlots();

        inStacks.forEach(drop -> {
            ItemStack left = null;
            for (int slot = 0; slot < itemHandlerSlots; slot++) {
                left = InventoryUtil.tryMoveInItem(itemHandler, drop, slot);
                if (left.isEmpty()) break;
            }
            if (left != null && !left.isEmpty()) notMoved.add(left);
        });

        return notMoved;
    }

    private static ItemStack tryMoveInItem(IItemHandler itemHandler, ItemStack itemStack, int slot) {
        return itemHandler.insertItem(slot, itemStack, false);
    }

    @Nullable
    public static IItemHandler getItemHandlerAt(Level level, BlockPos blockPos, Direction direction) {
        BlockState blockstate = level.getBlockState(blockPos);

        if (blockstate.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);

            final var capability = blockEntity.getCapability(
                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction
            ).resolve();

            if (capability.isPresent()) {
                IItemHandler itemHandler = capability.get();
                return itemHandler;
            }
        }

        return null;
    }

    public static boolean isItemHandlerFull(IItemHandler container) {
        return getSlots(container).allMatch((index) -> {
            ItemStack itemstack = container.getStackInSlot(index);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static IntStream getSlots(IItemHandler itemHandler) {
        return IntStream.range(0, itemHandler.getSlots());
    }

}
