package com.victorbrndls.uberminer2.integration.jei;

import com.victorbrndls.uberminer2.item.OreAttractorItem;

import net.minecraft.world.item.ItemStack;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;

public class OreAttractorSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

    public static final OreAttractorSubtypeInterpreter INSTANCE = new OreAttractorSubtypeInterpreter();

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        final var tier = OreAttractorItem.getTierOrNull(itemStack);
        if (tier != null) return tier.name();
        return IIngredientSubtypeInterpreter.NONE;
    }

}
