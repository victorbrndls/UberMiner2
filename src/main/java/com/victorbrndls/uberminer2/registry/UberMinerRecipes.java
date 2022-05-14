package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.recipe.OreAttractorRecipeMaker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class UberMinerRecipes {

    private static final Supplier<RecipeSerializer<?>> ORE_ATTRACTOR_RECIPE = () -> new SimpleRecipeSerializer<>(
            (id) -> OreAttractorRecipeMaker.getRecipes().stream().filter(cr -> cr.getId().equals(id)).findFirst()
                                           .orElse(null) //
    ).setRegistryName(new ResourceLocation(UberMiner.MOD_ID, "shaped_recipe"));

    public static void init(IForgeRegistry<RecipeSerializer<?>> registry) {
        registry.registerAll(ORE_ATTRACTOR_RECIPE.get());
    }

}
