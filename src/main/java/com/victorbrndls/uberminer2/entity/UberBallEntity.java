package com.victorbrndls.uberminer2.entity;

import static com.victorbrndls.uberminer2.util.BlockUtil.isOre;

import com.mojang.datafixers.util.Pair;
import com.victorbrndls.uberminer2.item.UberBallUpgrades;
import com.victorbrndls.uberminer2.registry.UberMinerEntities;
import com.victorbrndls.uberminer2.registry.UberMinerItems;
import com.victorbrndls.uberminer2.util.ItemStackUtil;
import com.victorbrndls.uberminer2.util.LootContextUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UberBallEntity extends ThrowableItemProjectile {

    private int range;
    private List<UberBallUpgrades> upgrades;

    public UberBallEntity(EntityType<? extends UberBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public UberBallEntity(Level level, LivingEntity livingEntity, int range, List<UberBallUpgrades> upgrades) {
        super(UberMinerEntities.UBER_BALL.get(), livingEntity, level);
        this.range = range;
        this.upgrades = upgrades;
    }

    public UberBallEntity(Level level, double x, double y, double z) {
        super(UberMinerEntities.UBER_BALL.get(), x, y, z, level);
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

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        discard();
        // TODO add cool sound
        extractOresAround(hitResult.getBlockPos());
    }

    private void extractOresAround(BlockPos pos) {
        if (!this.level.isClientSide) {
            var ores = getOresToMine(pos, range);

            if (ores.isEmpty()) return;

            ores.forEach((pair) -> {
                var block = pair.getFirst();
                level.setBlock(block, Blocks.STONE.defaultBlockState(), 3);
            });

            LootContext.Builder lootContextBuilder = LootContextUtil.getLootContextBuilder(level, pos);
            var drops = ores.stream().flatMap((pair) -> {
                var blockState = pair.getSecond();
                return blockState.getDrops(lootContextBuilder).stream();
            }).toList();

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

    private void spawnOreDrops(BlockPos blockHitPosition, List<ItemStack> drops) {
        var spawnPos = getSpawnDropsPosition(blockHitPosition);
        ItemStackUtil.drop(level, spawnPos, drops);
    }

    private BlockPos getSpawnDropsPosition(BlockPos entityHitPos) {
        if (upgrades.contains(UberBallUpgrades.ADD_ITEMS_TO_PLAYER_INVENTORY)) {
            return getOwner().blockPosition();
        } else {
            return entityHitPos;
        }
    }

    protected Item getDefaultItem() {
        return UberMinerItems.UBER_BALL_TIER_1.get();
    }
}
