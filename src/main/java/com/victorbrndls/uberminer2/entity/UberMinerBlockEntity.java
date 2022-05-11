package com.victorbrndls.uberminer2.entity;

import static com.victorbrndls.uberminer2.util.OreUtil.isOre;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.block.UberMinerBlock;
import com.victorbrndls.uberminer2.energy.UberEnergyStorage;
import com.victorbrndls.uberminer2.energy.UberEnergyStorageImpl;
import com.victorbrndls.uberminer2.gui.menu.UberMinerContainer;
import com.victorbrndls.uberminer2.item.UberTier;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;
import com.victorbrndls.uberminer2.util.InventoryUtil;
import com.victorbrndls.uberminer2.util.ItemStackUtil;
import com.victorbrndls.uberminer2.util.LootContextUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

public class UberMinerBlockEntity extends BaseContainerBlockEntity {

    private final UberTier uberTier;
    private final int chunkRadius;

    /**
     * Goes from 0 to {@link totalOperationTime}. Represents ticks since last operation.
     */
    public int operationTime = 0;
    public final int totalOperationTime;
    private final int operationEnergyCost;

    /**
     * Contains all ores the miner mined or will mine
     */
    private List<BlockPos> scannedOres;
    public int scannedOresCount = 0;
    /**
     * Returns the next ore to mine
     */
    private Iterator<BlockPos> oresToMine;
    /**
     * Incremented every time an ore is mined
     */
    public int oresMined = 0;

    private final UberEnergyStorage energy = new UberEnergyStorageImpl(32000);
    private final LazyOptional<IEnergyStorage> energyProxyCapability = LazyOptional.of(() -> energy);

    public UberMinerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(UberMinerBlockEntities.UBER_MINER.get(), blockPos, blockState);
    }

    public UberMinerBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);

        uberTier = extractBlockTier(blockState);
        chunkRadius = extractChunkRadius();
        totalOperationTime = extractTotalOperationTime();
        operationEnergyCost = extractOperationEnergyCost();
    }

    private int extractChunkRadius() {
        return switch (uberTier) {
            case TIER_I -> 1;
            case TIER_II -> 2;
            case TIER_III -> 3;
        };
    }

    private int extractTotalOperationTime() {
        return switch (uberTier) {
            case TIER_I -> 160;
            case TIER_II -> 120;
            case TIER_III -> 60;
        };
    }

    private int extractOperationEnergyCost() {
        return switch (uberTier) {
            case TIER_I -> 4000;
            case TIER_II -> 5000;
            case TIER_III -> 6500;
        };
    }

    private UberTier extractBlockTier(BlockState blockState) {
        if (blockState.getBlock() instanceof UberMinerBlock uberMinerBlock) {
            return uberMinerBlock.getUberTier();
        } else {
            return UberTier.TIER_I;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            energy.receiveEnergy(tag.getInt("Energy"), false);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyProxyCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected Component getDefaultName() {
        final var tier = switch (uberTier) {
            case TIER_I -> "I";
            case TIER_II -> "II";
            case TIER_III -> "III";
        };
        return Component.nullToEmpty("Uber Miner " + tier);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new UberMinerContainer(containerId, level, getBlockPos(), inventory, inventory.player);
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
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
    }

    public int getStoredEnergy() {
        return energy.getEnergyStored();
    }

    public void setStoredEnergy(int energy) {
        this.energy.restoreEnergy(energy);
    }

    public int getMaxStoredEnergy() {
        return energy.getMaxEnergyStored();
    }

    public int getOresMined() {
        return oresMined;
    }

    public int getTotalScannedOres() {
        return scannedOresCount;
    }

    private void tick() {
        if (energy.getEnergyStored() < operationEnergyCost) {
            operationTime = 0;
            return;
        }

        operationTime++;
        if (operationTime < totalOperationTime) return;
        operationTime = 0;

        if (scannedOres == null) scanOres();
        if (!oresToMine.hasNext()) return;

        energy.extractEnergy(operationEnergyCost, false);

        final var nextOrePos = oresToMine.next();
        final var nextOreBlockState = level.getBlockState(nextOrePos);

        // block might have been removed since it was scanned
        if (!isOre(nextOreBlockState)) return;

        // TODO replace nether ores with netherrack, ...
        level.setBlock(nextOrePos, Blocks.STONE.defaultBlockState(), 3);
        oresMined++;

        final var drops = nextOreBlockState.getDrops(LootContextUtil.getLootContextBuilder(level, nextOrePos));
        insertOrDropOres(drops);
    }

    private void scanOres() {
        if (level == null) return;
        LevelChunk chunk = level.getChunkAt(getBlockPos());
        ChunkPos centralChunkPos = chunk.getPos();
        final var distance = chunkRadius <= 1 ? 0 : chunkRadius - 1;

        ChunkPos firstChunk = level.getChunk(centralChunkPos.x - distance, centralChunkPos.z - distance).getPos();
        ChunkPos lastChunk = level.getChunk(centralChunkPos.x + distance, centralChunkPos.z + distance).getPos();

        final var blocksToScan = BlockPos.betweenClosed(firstChunk.getMinBlockX(), level.getMinBuildHeight(),
                                                        firstChunk.getMinBlockZ(), lastChunk.getMaxBlockX(),
                                                        Math.max(level.getMinBuildHeight(), getBlockPos().getY() - 1),
                                                        lastChunk.getMaxBlockZ());

        scannedOres = new ArrayList<>();

        blocksToScan.forEach(blockPos -> {
            final var block = level.getBlockState(blockPos);
            if (isOre(block)) scannedOres.add(blockPos.immutable());
        });
        UberMiner.LOGGER.debug("Found {} ores", scannedOres.size());

        oresToMine = scannedOres.iterator();
        scannedOresCount = scannedOres.size();
    }

    private void insertOrDropOres(List<ItemStack> drops) {
        final var spawnPos = getBlockPos().above();
        IItemHandler itemHandler = getItemHandler();

        if (itemHandler != null) {
            final var notDropped = InventoryUtil.tryMoveInItems(itemHandler, drops);
            if (!notDropped.isEmpty()) ItemStackUtil.drop(level, spawnPos, notDropped);
        } else {
            ItemStackUtil.drop(level, spawnPos, drops);
        }
    }

    @Nullable
    private IItemHandler getItemHandler() {
        BlockPos blockPos = getBlockPos().above();
        return InventoryUtil.getItemHandlerAt(level, blockPos, Direction.DOWN);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T blockEntity) {
        ((UberMinerBlockEntity) blockEntity).tick();
    }

}
