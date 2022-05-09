package com.victorbrndls.uberminer2.energy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface UberEnergyStorage extends IEnergyStorage {

    void restoreEnergy(int energy);

}
