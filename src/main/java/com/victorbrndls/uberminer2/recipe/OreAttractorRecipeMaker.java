package com.victorbrndls.uberminer2.recipe;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.item.OreAttractorItem;
import com.victorbrndls.uberminer2.item.OreAttractorTier;
import com.victorbrndls.uberminer2.registry.UberMinerItems;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Arrays;
import java.util.List;

public class OreAttractorRecipeMaker {

    public static List<CraftingRecipe> getRecipes() {
        final var TIER_I =
                getRecipe("ore_attractor_tier1", Ingredient.of(Items.IRON_BLOCK), OreAttractorTier.TIER_I);
        final var TIER_II =
                getRecipe("ore_attractor_tier2", Ingredient.of(Items.GOLD_BLOCK), OreAttractorTier.TIER_II);
        final var TIER_III =
                getRecipe("ore_attractor_tier3", Ingredient.of(Items.DIAMOND_BLOCK), OreAttractorTier.TIER_III);

        return Arrays.asList(TIER_I, TIER_II, TIER_III);
    }

    private static CraftingRecipe getRecipe(String idPath, Ingredient tieredIngredient, OreAttractorTier tier) {
        Ingredient uberBallTier3Ingredient = Ingredient.of(UberMinerItems.UBER_BALL_TIER_3.get());
        Ingredient redstoneIngredient = Ingredient.of(Items.REDSTONE);

        final var inputs = NonNullList.of(
                Ingredient.EMPTY,
                redstoneIngredient, uberBallTier3Ingredient, redstoneIngredient,
                uberBallTier3Ingredient, tieredIngredient, uberBallTier3Ingredient,
                redstoneIngredient, uberBallTier3Ingredient, redstoneIngredient
        );
        final var result = OreAttractorItem.createFromTier(tier);

        ResourceLocation id = new ResourceLocation(UberMiner.MOD_ID, idPath);
        return new ShapedRecipe(id, "ore_attractor.group", 3, 3, inputs, result);
    }

}
