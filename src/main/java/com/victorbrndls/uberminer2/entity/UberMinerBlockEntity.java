package com.victorbrndls.uberminer2.entity;

import static com.victorbrndls.uberminer2.util.OreUtil.isOre;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.gui.menu.UberMinerContainer;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;
import com.victorbrndls.uberminer2.util.InventoryUtil;
import com.victorbrndls.uberminer2.util.ItemStackUtil;
import com.victorbrndls.uberminer2.util.LootContextUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

public class UberMinerBlockEntity extends BaseContainerBlockEntity {

    /**
     * Goes from 0 to {@link totalOperationTime}. Represents ticks since last operation.
     */
    public int operationTime = 0;
    public final int totalOperationTime = 60;
    private final int operationEnergyCost = 4000;

    /**
     * Contains all ores the miner mined or will mine
     */
    private List<BlockPos> scannedOres;
    /**
     * Returns the next ore to mine
     */
    private Iterator<BlockPos> oresToMine;

    private final IEnergyStorage energy = new EnergyStorage(32000);
    private final LazyOptional<IEnergyStorage> energyProxyCapability = LazyOptional.of(() -> energy);

    public UberMinerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(UberMinerBlockEntities.UBER_MINER.get(), blockPos, blockState);
    }

    public UberMinerBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
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
        return Component.nullToEmpty("Uber Miner I");
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
        return null;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return null;
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

    private void tick() {
        operationTime++;
        if (operationTime < totalOperationTime) return;
        operationTime = 0;

        if (scannedOres == null) scanOres();
        if (!oresToMine.hasNext()) return;

        if (energy.getEnergyStored() < operationEnergyCost) return;
        energy.extractEnergy(operationEnergyCost, false);

        final var nextOrePos = oresToMine.next();
        final var nextOreBlockState = level.getBlockState(nextOrePos);

        // block might have been removed since it was scanned
        if (!isOre(nextOreBlockState)) return;

        // TODO replace nether ores with netherrack, ...
        level.setBlock(nextOrePos, Blocks.STONE.defaultBlockState(), 3);

        final var drops = nextOreBlockState.getDrops(LootContextUtil.getLootContextBuilder(level, nextOrePos));
        insertOrDropOres(drops);
    }

    private void scanOres() {
        if (level == null) return;
        LevelChunk chunk = level.getChunkAt(getBlockPos());
        ChunkPos chunkPos = chunk.getPos();

        final var blocksToScan = BlockPos.betweenClosed(chunkPos.getMinBlockX(), level.getMinBuildHeight(),
                                                        chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(),
                                                        Math.max(level.getMinBuildHeight(), getBlockPos().getY() - 1),
                                                        chunkPos.getMaxBlockZ());

        scannedOres = new ArrayList<>();

        blocksToScan.forEach(blockPos -> {
            final var block = level.getBlockState(blockPos);
            if (isOre(block)) scannedOres.add(blockPos.immutable());
        });
        UberMiner.LOGGER.debug("Found {} ores", scannedOres.size());

        oresToMine = scannedOres.iterator();
    }

    private void insertOrDropOres(List<ItemStack> drops) {
        final var spawnPos = getBlockPos().above();
        Container container = getDropContainer();

        if (container != null) {
            final var notDropped = InventoryUtil.tryMoveInItems(container, drops, Direction.DOWN);
            if (!notDropped.isEmpty()) ItemStackUtil.drop(level, spawnPos, notDropped);
        } else {
            ItemStackUtil.drop(level, spawnPos, drops);
        }
    }

    @Nullable
    private Container getDropContainer() {
        BlockPos blockPos = getBlockPos().above();
        return InventoryUtil.getContainerAt(level, blockPos);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T blockEntity) {
        ((UberMinerBlockEntity) blockEntity).tick();
    }

}
