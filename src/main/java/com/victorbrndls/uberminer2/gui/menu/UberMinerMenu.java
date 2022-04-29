package com.victorbrndls.uberminer2.gui.menu;

import com.victorbrndls.uberminer2.registry.UberMinerMenus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class UberMinerMenu extends AbstractContainerMenu {

    public UberMinerMenu(int containerId, Inventory inventory) {
        super(UberMinerMenus.UBER_MINER.get(), containerId);

        for(int x = 0; x < 3; ++x)
            for(int y = 0; y < 9; ++y)
                addSlot(new Slot(inventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));

        for(int x = 0; x < 9; ++x)
            addSlot(new Slot(inventory, x, 8 + x * 18, 142));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
