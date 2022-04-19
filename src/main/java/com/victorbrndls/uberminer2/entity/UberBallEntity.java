package com.victorbrndls.uberminer2.entity;

import com.victorbrndls.uberminer2.item.UberMinerItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class UberBallEntity extends ThrowableItemProjectile {

    public UberBallEntity(EntityType<? extends UberBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public UberBallEntity(Level level, LivingEntity livingEntity) {
        super(UberMinerEntities.UBER_BALL.get(), livingEntity, level);
    }

    public UberBallEntity(Level level, double p_37477_, double p_37478_, double p_37479_) {
        super(UberMinerEntities.UBER_BALL.get(), p_37477_, p_37478_, p_37479_, level);
    }

    public void handleEntityEvent(byte p_37484_) {
        if (p_37484_ == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(),
                        this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }

//    protected void onHitEntity(EntityHitResult hitResult) {
//        super.onHitEntity(hitResult);
//        hitResult.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
//    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);

    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                for (int j = 0; j < i; ++j) {
                    Chicken chicken = EntityType.CHICKEN.create(this.level);
                    chicken.setAge(-24000);
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level.addFreshEntity(chicken);
                }
            }

            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }

    }

    protected Item getDefaultItem() {
        return UberMinerItems.UBER_BALL.get();
    }
}
