package com.victorbrndls.uberminer2.gui.menu;

import com.victorbrndls.uberminer2.entity.ResourceMinerBlockEntity;
import com.victorbrndls.uberminer2.registry.UberMinerMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class ResourceMinerContainer extends AbstractContainerMenu {

    private final ResourceMinerBlockEntity blockEntity;

    public ResourceMinerContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(UberMinerMenus.RESOURCE_MINER.get(), windowId);
        this.blockEntity = (ResourceMinerBlockEntity) world.getBlockEntity(pos);

        addDataSlots();

        for (int x = 0; x < 3; ++x)
            for (int y = 0; y < 9; ++y)
                addSlot(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));

        for (int x = 0; x < 9; ++x)
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
    }

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.operationTime;
            }

            @Override
            public void set(int value) {
                blockEntity.operationTime = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getStoredEnergy();
            }

            @Override
            public void set(int value) {
                blockEntity.setStoredEnergy(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.blocksBroken;
            }

            @Override
            public void set(int value) {
                blockEntity.blocksBroken = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.scannedBlocksCount;
            }

            @Override
            public void set(int value) {
                blockEntity.scannedBlocksCount = value;
            }
        });

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public int getMaxStoredEnergy() {
        return blockEntity.getMaxStoredEnergy();
    }

    public int getStoredEnergy() {
        return blockEntity.getStoredEnergy();
    }

    public float getStoredEnergyPercentage() {
        return getStoredEnergy() / (float) getMaxStoredEnergy();
    }

    public int getOperationTime() {
        if (isFinished()) return 0;
        return blockEntity.operationTime;
    }

    public int getTotalOperationTime() {
        return blockEntity.totalOperationTime;
    }

    public float getProgressPercentage() {
        if (isFinished()) return 0f;
        return blockEntity.operationTime / (float) blockEntity.totalOperationTime;
    }

    public int getBlocksBroken() {
        return blockEntity.getBlocksBroken();
    }

    public int getScannedBlocks() {
        return blockEntity.getTotalScannedBlocks();
    }

    public float getBrokenBlocksProgress() {
        return getBlocksBroken() / (float) getScannedBlocks();
    }

    public boolean isFinished() {
        return getBlocksBroken() >= getScannedBlocks();
    }

}
