package com.victorbrndls.uberminer2.entity;

import static com.victorbrndls.uberminer2.util.OreUtil.isOre;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;
import com.victorbrndls.uberminer2.registry.UberMinerMenus;
import com.victorbrndls.uberminer2.util.ItemStackUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

public class UberMinerBlockEntity extends BaseContainerBlockEntity {

    private int ticksSinceLastOperation = 0;
    private final int operationCost = 60;

    /**
     * Contains all ores the miner mined or will mine
     */
    private List<BlockPos> scannedOres;
    /**
     * Returns the next ore to mine
     */
    private Iterator<BlockPos> oresToMine;

    public UberMinerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(UberMinerBlockEntities.UBER_MINER.get(), blockPos, blockState);
    }

    public UberMinerBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // other
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // other
    }

    @Override
    protected Component getDefaultName() {
        return Component.nullToEmpty("Uber Miner I");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return UberMinerMenus.UBER_MINER.get().create(containerId, inventory);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return true;
    }

    @Override
    public void clearContent() {
    }

    private void tick() {
        ticksSinceLastOperation++;
        if (ticksSinceLastOperation < operationCost) return;
        ticksSinceLastOperation = 0;

        if (scannedOres == null) scanOres();
        if (!oresToMine.hasNext()) return;

        final var nextOrePos = oresToMine.next();
        final var nextOreBlockState = level.getBlockState(nextOrePos);

        // block might have been removed since it was scanned
        if (!isOre(nextOreBlockState)) return;

        // TODO replace nether ores with netherrack, ...
        level.setBlock(nextOrePos, Blocks.STONE.defaultBlockState(), 3);

        final var drops = nextOreBlockState.getDrops(getLootContextBuilder(nextOrePos));
        spawnOreDrops(getBlockPos().above(), drops);
    }

    private void scanOres() {
        if (level == null) return;
        LevelChunk chunk = level.getChunkAt(getBlockPos());
        ChunkPos chunkPos = chunk.getPos();

        final var blocksToScan = BlockPos.betweenClosed(chunkPos.getMinBlockX(), level.getMinBuildHeight(),
                chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), Math.max(level.getMinBuildHeight(),
                        getBlockPos().getY() - 1), chunkPos.getMaxBlockZ());

        scannedOres = new ArrayList<>();

        blocksToScan.forEach(blockPos -> {
            final var block = level.getBlockState(blockPos);
            if (isOre(block)) scannedOres.add(blockPos.immutable());
        });
        UberMiner.LOGGER.debug("Found {} ores", scannedOres.size());

        oresToMine = scannedOres.iterator();
    }

    @NotNull
    private LootContext.Builder getLootContextBuilder(BlockPos pos) {
        // TODO unify with UberBallEntity
        return (new LootContext.Builder((ServerLevel) level)).withRandom(level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE));
    }

    private void spawnOreDrops(BlockPos spawnPos, List<ItemStack> drops) {
        Container container = getDropContainer();
        List<ItemStack> notDropped = new ArrayList<>();

        if (container != null && !isFullContainer(container)) {
            final var containerSize = container.getContainerSize();
            drops.forEach(drop -> {
                ItemStack left = null;
                for (int slot = 0; slot < containerSize; slot++) {
                    left = tryMoveInItem(container, drop, slot, Direction.DOWN);
                    if (left.isEmpty()) break;
                }
                if (left != null && !left.isEmpty()) notDropped.add(left);
            });
            container.setChanged();
            if (notDropped.isEmpty()) return;
        }

        if (notDropped.isEmpty()) {
            ItemStackUtil.drop(level, spawnPos, drops);
        } else {
            ItemStackUtil.drop(level, spawnPos, notDropped);
        }
    }

    private ItemStack tryMoveInItem(Container destination, ItemStack inStack, int slot, @Nullable Direction direction) {
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

    private boolean canPlaceItemInContainer(Container container, ItemStack stack, int slot,
                                            @Nullable Direction direction) {
        if (!container.canPlaceItem(slot, stack)) {
            return false;
        } else {
            return !(container instanceof WorldlyContainer) || ((WorldlyContainer) container).canPlaceItemThroughFace(slot, stack, direction);
        }
    }

    @Nullable
    private Container getDropContainer() {
        BlockPos blockPos = getBlockPos().above();
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

    private boolean isFullContainer(Container container) {
        return getSlots(container, Direction.DOWN).allMatch((index) -> {
            ItemStack itemstack = container.getItem(index);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private IntStream getSlots(Container container, Direction direction) {
        return container instanceof WorldlyContainer ?
                IntStream.of(((WorldlyContainer) container).getSlotsForFace(direction)) : IntStream.range(0,
                container.getContainerSize());
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

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T blockEntity) {
        ((UberMinerBlockEntity) blockEntity).tick();
    }

}
