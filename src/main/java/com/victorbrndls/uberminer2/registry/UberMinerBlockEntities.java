package com.victorbrndls.uberminer2.registry;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.entity.ResourceMinerBlockEntity;
import com.victorbrndls.uberminer2.entity.UberMinerBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UberMinerBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, UberMiner.MOD_ID);

    // Blocks
    public static final RegistryObject<BlockEntityType<UberMinerBlockEntity>> UBER_MINER =
            BLOCK_ENTITIES.register("entity_uber_miner", () -> BlockEntityType.Builder.of(UberMinerBlockEntity::new,
                                                                                          UberMinerBlocks.UBER_MINER_TIER_1.get(),
                                                                                          UberMinerBlocks.UBER_MINER_TIER_2.get(),
                                                                                          UberMinerBlocks.UBER_MINER_TIER_3.get())
                                                                                      .build(null));

    public static final RegistryObject<BlockEntityType<ResourceMinerBlockEntity>> RESOURCE_MINER =
            BLOCK_ENTITIES.register("entity_resource_miner",
                                    () -> BlockEntityType.Builder.of(ResourceMinerBlockEntity::new,
                                                                     UberMinerBlocks.RESOURCE_MINER.get())
                                                                 .build(null));

    public static void init() {
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
