package com.victorbrndls.uberminer2.integration.jei;

import com.victorbrndls.uberminer2.UberMiner;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;

@JeiPlugin
public class UberPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(UberMiner.MOD_ID, "jei");
    }

}