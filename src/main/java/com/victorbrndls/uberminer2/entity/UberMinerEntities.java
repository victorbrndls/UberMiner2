package com.victorbrndls.uberminer2.entity;

import com.victorbrndls.uberminer2.UberMiner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UberMinerEntities {

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
            UberMiner.MOD_ID);

    public static final RegistryObject<EntityType<UberBallEntity>> UBER_BALL =
            register("thrown_uber_ball",
                    EntityType.Builder.<UberBallEntity>of(UberBallEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4).updateInterval(10));

    public static void init() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name,
                                                                             EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(name));
    }

}
