package com.victorbrndls.uberminer2.recipe;

import com.victorbrndls.uberminer2.item.OreAttractorItem;
import com.victorbrndls.uberminer2.item.OreAttractorTier;
import com.victorbrndls.uberminer2.registry.UberMinerItems;
import com.victorbrndls.uberminer2.registry.UberMinerRecipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class OreAttractorRecipe extends CustomRecipe {

    private static final Ingredient TIERED_INGREDIENT = Ingredient.of(
            Items.IRON_BLOCK, Items.GOLD_BLOCK, Items.DIAMOND_BLOCK
    );
    private static final Ingredient UBER_BALL_TIER_III_INGREDIENT = Ingredient.of(
            UberMinerItems.UBER_BALL_TIER_3.get()
    );
    private static final Ingredient REDSTONE_INGREDIENT = Ingredient.of(Items.REDSTONE);

    private static final List<Ingredient> SHAPED_RECIPE = Arrays.asList(
            REDSTONE_INGREDIENT,
            UBER_BALL_TIER_III_INGREDIENT,
            REDSTONE_INGREDIENT,
            UBER_BALL_TIER_III_INGREDIENT,
            TIERED_INGREDIENT,
            UBER_BALL_TIER_III_INGREDIENT,
            REDSTONE_INGREDIENT,
            UBER_BALL_TIER_III_INGREDIENT,
            REDSTONE_INGREDIENT
    );

    public OreAttractorRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        for (int i = 0; i < SHAPED_RECIPE.size(); i++) {
            final var item = container.getItem(i);
            if (!SHAPED_RECIPE.get(i).test(item)) return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack itemStack = new ItemStack(UberMinerItems.ORE_ATTRACTOR.get(), 1);
        CompoundTag tag = itemStack.getOrCreateTagElement(OreAttractorItem.TAG_ELEMENT);

        ItemStack tierItem = container.getItem(getTierItemIndex());
        tag.putString(OreAttractorItem.TAG_ELEMENT_TIER, getTier(tierItem).name());

        return itemStack;
    }

    private int getTierItemIndex() {
        return SHAPED_RECIPE.indexOf(TIERED_INGREDIENT);
    }

    private OreAttractorTier getTier(ItemStack itemStack) {
        if (itemStack.is(Items.DIAMOND_BLOCK)) return OreAttractorTier.TIER_III;
        if (itemStack.is(Items.GOLD_BLOCK)) return OreAttractorTier.TIER_II;
        return OreAttractorTier.TIER_I;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return UberMinerRecipes.ORE_ATTRACTOR.get();
    }
}
