package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.item.OreAttractorItem;
import com.victorbrndls.uberminer2.item.OreAttractorTier;
import com.victorbrndls.uberminer2.item.UberBallItem;
import com.victorbrndls.uberminer2.item.UberBallUpgrades;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;

public class UberMinerItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                                                                                UberMiner.MOD_ID);

    public static final RegistryObject<Item> UBER_BALL_TIER_1 = ITEMS.register("uber_ball_tier1",
                                                                               () -> new UberBallItem(2,
                                                                                                      Collections.emptyList()));
    public static final RegistryObject<Item> UBER_BALL_TIER_2 = ITEMS.register("uber_ball_tier2",
                                                                               () -> new UberBallItem(2,
                                                                                                      List.of(UberBallUpgrades.ADD_ITEMS_TO_PLAYER_INVENTORY)));
    public static final RegistryObject<Item> UBER_BALL_TIER_3 = ITEMS.register("uber_ball_tier3",
                                                                               () -> new UberBallItem(3,
                                                                                                      List.of(UberBallUpgrades.ADD_ITEMS_TO_PLAYER_INVENTORY)));

    public static final RegistryObject<Item> UBER_MINER_TIER_1 = ITEMS.register("uber_miner_tier1", () -> new BlockItem(
            UberMinerBlocks.UBER_MINER_TIER_1.get(), new Item.Properties().stacksTo(1).tab(UberMiner.UBER_MINER_TAB)));
    public static final RegistryObject<Item> UBER_MINER_TIER_2 = ITEMS.register("uber_miner_tier2", () -> new BlockItem(
            UberMinerBlocks.UBER_MINER_TIER_2.get(), new Item.Properties().stacksTo(1).tab(UberMiner.UBER_MINER_TAB)));
    public static final RegistryObject<Item> UBER_MINER_TIER_3 = ITEMS.register("uber_miner_tier3", () -> new BlockItem(
            UberMinerBlocks.UBER_MINER_TIER_3.get(), new Item.Properties().stacksTo(1).tab(UberMiner.UBER_MINER_TAB)));

    public static final RegistryObject<Item> RESOURCE_MINER = ITEMS.register("resource_miner", () -> new BlockItem(
            UberMinerBlocks.RESOURCE_MINER.get(), new Item.Properties().stacksTo(1).tab(UberMiner.UBER_MINER_TAB)));

    public static final RegistryObject<Item> ORE_ATTRACTOR = ITEMS.register("ore_attractor", OreAttractorItem::new);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
