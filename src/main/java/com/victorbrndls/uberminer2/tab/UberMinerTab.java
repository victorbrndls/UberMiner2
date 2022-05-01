package com.victorbrndls.uberminer2.tab;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.registry.UberMinerItems;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class UberMinerTab extends CreativeModeTab {

    public UberMinerTab() {
        super(UberMiner.MOD_ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(UberMinerItems.UBER_BALL_TIER_3.get());
    }
}
