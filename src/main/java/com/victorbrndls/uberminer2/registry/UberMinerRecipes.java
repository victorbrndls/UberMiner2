package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.recipe.OreAttractorRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UberMinerRecipes {

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, UberMiner.MOD_ID);

    public static final RegistryObject<SimpleRecipeSerializer<OreAttractorRecipe>> ORE_ATTRACTOR = RECIPES.register(
            "crafting_special_ore_attractor", () -> new SimpleRecipeSerializer<>(OreAttractorRecipe::new));

    public static void init() {
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
