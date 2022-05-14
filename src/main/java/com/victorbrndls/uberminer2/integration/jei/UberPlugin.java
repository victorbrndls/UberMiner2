package com.victorbrndls.uberminer2.integration.jei;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.item.OreAttractorItem;
import com.victorbrndls.uberminer2.registry.UberMinerItems;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;

@JeiPlugin
public class UberPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(UberMiner.MOD_ID, "jei");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, UberMinerItems.ORE_ATTRACTOR.get(),
                                                (itemStack, context) -> {
                                                    final var tier = OreAttractorItem.getTierOrNull(itemStack);
                                                    if (tier != null) return tier.name();
                                                    return IIngredientSubtypeInterpreter.NONE;
                                                });
    }

}