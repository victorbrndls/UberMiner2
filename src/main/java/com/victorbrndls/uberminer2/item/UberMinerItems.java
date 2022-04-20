package com.victorbrndls.uberminer2.item;

import com.victorbrndls.uberminer2.UberMiner;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UberMinerItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            UberMiner.MOD_ID);

    public static final RegistryObject<Item> UBER_BALL_TIER_1 = ITEMS.register("uber_ball_tier1",
            () -> new UberBallItem(2, Collections.emptyList()));
    public static final RegistryObject<Item> UBER_BALL_TIER_2 = ITEMS.register("uber_ball_tier2",
            () -> new UberBallItem(2, List.of(UberBallUpgrades.ADD_ITEMS_TO_PLAYER_INVENTORY)));
    public static final RegistryObject<Item> UBER_BALL_TIER_3 = ITEMS.register("uber_ball_tier3",
            () -> new UberBallItem(3, List.of(UberBallUpgrades.ADD_ITEMS_TO_PLAYER_INVENTORY)));

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
