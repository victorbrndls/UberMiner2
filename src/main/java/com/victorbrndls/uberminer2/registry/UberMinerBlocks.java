package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.block.UberMinerBlock;
import com.victorbrndls.uberminer2.item.UberTier;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UberMinerBlocks {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
                                                                                  UberMiner.MOD_ID);

    public static final RegistryObject<Block> UBER_MINER_TIER_1 = BLOCKS.register("uber_miner_tier1",
                                                                                  () -> new UberMinerBlock(
                                                                                          UberTier.TIER_I));
    public static final RegistryObject<Block> UBER_MINER_TIER_2 = BLOCKS.register("uber_miner_tier2",
                                                                                  () -> new UberMinerBlock(
                                                                                          UberTier.TIER_II));
    public static final RegistryObject<Block> UBER_MINER_TIER_3 = BLOCKS.register("uber_miner_tier3",
                                                                                  () -> new UberMinerBlock(
                                                                                          UberTier.TIER_III));

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
