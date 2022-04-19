package com.victorbrndls.uberminer2.tab;

import com.victorbrndls.uberminer2.item.UberMinerItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class UberMinerTab extends CreativeModeTab {

    public UberMinerTab() {
        super("Uber Miner 2");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(UberMinerItems.UBER_BALL.get());
    }
}
