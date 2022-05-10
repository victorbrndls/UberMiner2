package com.victorbrndls.uberminer2.gui.menu;

import com.victorbrndls.uberminer2.entity.UberMinerBlockEntity;
import com.victorbrndls.uberminer2.registry.UberMinerMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class UberMinerContainer extends AbstractContainerMenu {

    private final UberMinerBlockEntity blockEntity;

    public UberMinerContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(UberMinerMenus.UBER_MINER.get(), windowId);
        this.blockEntity = (UberMinerBlockEntity) world.getBlockEntity(pos);

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
                return blockEntity.oresMined;
            }

            @Override
            public void set(int value) {
                blockEntity.oresMined = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.scannedOresCount;
            }

            @Override
            public void set(int value) {
                blockEntity.scannedOresCount = value;
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
        return blockEntity.operationTime;
    }

    public int getTotalOperationTime() {
        return blockEntity.totalOperationTime;
    }

    public float getProgressPercentage() {
        return blockEntity.operationTime / (float) blockEntity.totalOperationTime;
    }

    public int getOresMined() {
        return blockEntity.getOresMined();
    }

    public int getScannedOres() {
        return blockEntity.getTotalScannedOres();
    }

    public float getMinedOresProgress() {
        return getOresMined() / (float) getScannedOres();
    }

}
