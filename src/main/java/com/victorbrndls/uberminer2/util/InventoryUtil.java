package com.victorbrndls.uberminer2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

public class InventoryUtil {

    public static List<ItemStack> tryMoveInItems(
            Container destination, List<ItemStack> inStacks,
            @Nullable Direction direction) {
        if (InventoryUtil.isFullContainer(destination)) return inStacks;
        List<ItemStack> notMoved = new ArrayList<>();

        final var destinationSize = destination.getContainerSize();

        inStacks.forEach(drop -> {
            ItemStack left = null;
            for (int slot = 0; slot < destinationSize; slot++) {
                left = InventoryUtil.tryMoveInItem(destination, drop, slot, direction);
                if (left.isEmpty()) break;
            }
            if (left != null && !left.isEmpty()) notMoved.add(left);
        });

        if (!inStacks.equals(notMoved)) destination.setChanged();
        return notMoved;
    }

    public static ItemStack tryMoveInItem(
            Container destination, ItemStack inStack, int slot,
            @Nullable Direction direction) {
        ItemStack itemStack = destination.getItem(slot);

        if (canPlaceItemInContainer(destination, inStack, slot, direction)) {
            if (itemStack.isEmpty()) {
                destination.setItem(slot, inStack);
                inStack = ItemStack.EMPTY;
            } else if (canMergeItems(itemStack, inStack)) {
                int availableSpace = inStack.getMaxStackSize() - itemStack.getCount();
                int itemsToMove = Math.min(inStack.getCount(), availableSpace);
                inStack.shrink(itemsToMove);
                itemStack.grow(itemsToMove);
            }
        }

        return inStack;
    }

    @Nullable
    public static Container getContainerAt(Level level, BlockPos blockPos) {
        BlockState blockstate = level.getBlockState(blockPos);
        Block block = blockstate.getBlock();

        if (blockstate.hasBlockEntity()) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof Container container) {
                if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    return ChestBlock.getContainer((ChestBlock) block, blockstate, level, blockPos, true);
                }
            }
        }

        return null;
    }

    public static boolean isFullContainer(Container container) {
        return getSlots(container, Direction.DOWN).allMatch((index) -> {
            ItemStack itemstack = container.getItem(index);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static IntStream getSlots(Container container, Direction direction) {
        return container instanceof WorldlyContainer ?
                IntStream.of(((WorldlyContainer) container).getSlotsForFace(direction)) : IntStream.range(0,
                                                                                                          container.getContainerSize());
    }

    private static boolean canPlaceItemInContainer(
            Container container, ItemStack stack, int slot,
            @Nullable Direction direction) {
        if (!container.canPlaceItem(slot, stack)) {
            return false;
        } else {
            return !(container instanceof WorldlyContainer) || ((WorldlyContainer) container).canPlaceItemThroughFace(
                    slot, stack, direction);
        }
    }

    private static boolean canMergeItems(ItemStack firstStack, ItemStack secondStack) {
        if (!firstStack.is(secondStack.getItem())) {
            return false;
        } else if (firstStack.getDamageValue() != secondStack.getDamageValue()) {
            return false;
        } else if (firstStack.getCount() >= firstStack.getMaxStackSize()) {
            return false;
        } else {
            return ItemStack.tagMatches(firstStack, secondStack);
        }
    }

}
