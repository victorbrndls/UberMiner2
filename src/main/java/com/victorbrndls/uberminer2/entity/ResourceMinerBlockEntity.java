package com.victorbrndls.uberminer2.entity;

import static com.victorbrndls.uberminer2.util.BlockUtil.isResource;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.energy.UberEnergyStorage;
import com.victorbrndls.uberminer2.energy.UberEnergyStorageImpl;
import com.victorbrndls.uberminer2.gui.menu.ResourceMinerContainer;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResourceMinerBlockEntity extends BaseContainerBlockEntity {

    /**
     * Goes from 0 to {@link totalOperationTime}. Represents ticks since last operation.
     */
    public int operationTime = 0;
    public final int totalOperationTime = 20;
    private final int operationEnergyCost = 2000;

    /**
     * Contains all ores the miner mined or will mine
     */
    private List<BlockPos> scannedBlocks;
    public int scannedBlocksCount = 0;
    /**
     * Returns the next block to break
     */
    private Iterator<BlockPos> blocksToBreak;
    /**
     * Incremented every time a block is mined
     */
    public int blocksBroken = 0;

    private final UberEnergyStorage energy = new UberEnergyStorageImpl(32000);
    private final LazyOptional<IEnergyStorage> energyProxyCapability = LazyOptional.of(() -> energy);

    public ResourceMinerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(UberMinerBlockEntities.RESOURCE_MINER.get(), blockPos, blockState);
    }

    public ResourceMinerBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
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
        return Component.nullToEmpty("Resource Miner ");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ResourceMinerContainer(containerId, level, getBlockPos(), inventory, inventory.player);
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

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public int getTotalScannedBlocks() {
        return scannedBlocksCount;
    }

    private void tick() {
        if (energy.getEnergyStored() < operationEnergyCost) {
            operationTime = 0;
            return;
        }

        operationTime++;
        if (operationTime < totalOperationTime) return;
        operationTime = 0;

        if (scannedBlocks == null) scanBlocks();
        if (!blocksToBreak.hasNext()) return;

        energy.extractEnergy(operationEnergyCost, false);

        final var nextBlockPos = blocksToBreak.next();
        final var nextBlockBlockState = level.getBlockState(nextBlockPos);

        // block might have been removed since it was scanned
        if (!isResource(nextBlockBlockState)) return;

        level.setBlock(nextBlockPos, Blocks.AIR.defaultBlockState(), 3);
        blocksBroken++;
    }

    private void scanBlocks() {
        if (level == null) return;
        LevelChunk chunk = level.getChunkAt(getBlockPos());
        ChunkPos chunkPos = chunk.getPos();

        final var blocksToScan = BlockPos.betweenClosed(
                chunkPos.getMinBlockX(),
                level.getMinBuildHeight(),
                chunkPos.getMinBlockZ(),
                chunkPos.getMaxBlockX(),
                Math.max(level.getMinBuildHeight(), getBlockPos().getY() - 1),
                chunkPos.getMaxBlockZ()
        );

        scannedBlocks = new ArrayList<>();

        blocksToScan.forEach(blockPos -> {
            final var block = level.getBlockState(blockPos);
            if (isResource(block)) scannedBlocks.add(blockPos.immutable());
        });
        UberMiner.LOGGER.debug("Found {} blocks", scannedBlocks.size());

        blocksToBreak = scannedBlocks.iterator();
        scannedBlocksCount = scannedBlocks.size();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T blockEntity) {
        ((ResourceMinerBlockEntity) blockEntity).tick();
    }

}
