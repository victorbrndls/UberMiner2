package com.victorbrndls.uberminer2.entity;

import com.mojang.datafixers.util.Pair;
import com.victorbrndls.uberminer2.item.UberMinerItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

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
        discard();
        extractOresAround(hitResult.getBlockPos());
    }

    private void extractOresAround(BlockPos pos) {
        if (!this.level.isClientSide) {
            var range = 2;
            var ores = getOresToMine(pos, range);

            if (ores.isEmpty()) return;

            ores.forEach((pair) -> {
                var block = pair.getFirst();
                level.setBlock(block, Blocks.STONE.defaultBlockState(), 3);
            });

            LootContext.Builder lootContextBuilder = getLootContextBuilder(pos);
            var drops = ores.stream().flatMap((pair) -> {
                var blockState = pair.getSecond();
                return blockState.getDrops(lootContextBuilder).stream();
            });

            spawnOreDrops(pos, drops);
        }
    }

    @NotNull
    private ArrayList<Pair<BlockPos, BlockState>> getOresToMine(BlockPos pos, int range) {
        var ores = new ArrayList<Pair<BlockPos, BlockState>>();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    var blockPos = pos.offset(x, y, z);
                    var blockState = level.getBlockState(blockPos);

                    if (isOre(blockState)) ores.add(Pair.of(blockPos, blockState));
                }
            }
        }

        return ores;
    }

    private boolean isOre(BlockState blockState) {
        var oreResourceLocation = new ResourceLocation("forge", "ores");
        return blockState.getTags().anyMatch((tag) -> tag.location().equals(oreResourceLocation));
    }

    private void spawnOreDrops(BlockPos pos, Stream<ItemStack> drops) {
        drops.forEach((itemStack) -> {
            var itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            level.addFreshEntity(itemEntity);
        });
    }

    @NotNull
    private LootContext.Builder getLootContextBuilder(BlockPos pos) {
        return (new LootContext.Builder((ServerLevel) this.level))
                .withRandom(this.level.random)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, getOwner());
    }

    protected Item getDefaultItem() {
        return UberMinerItems.UBER_BALL.get();
    }
}
