package com.victorbrndls.uberminer2.energy;

import net.minecraftforge.energy.EnergyStorage;

public class UberEnergyStorageImpl extends EnergyStorage implements UberEnergyStorage {

    public UberEnergyStorageImpl(int capacity) {
        this(capacity, capacity, capacity);
    }

    public UberEnergyStorageImpl(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
    }

    @Override
    public void restoreEnergy(int energy) {
        this.energy = energy;
    }

}
