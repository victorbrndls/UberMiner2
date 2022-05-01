package com.victorbrndls.uberminer2.block;

import com.victorbrndls.uberminer2.entity.UberMinerBlockEntity;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class UberMinerBlock extends BaseEntityBlock {

    public UberMinerBlock() {
        super(BlockBehaviour.Properties.of(Material.DECORATION).strength(0.5F).sound(SoundType.METAL));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return UberMinerBlockEntities.UBER_MINER.get().create(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState blockState,
            BlockEntityType<T> entityType) {
        return level.isClientSide ? null : entityType == UberMinerBlockEntities.UBER_MINER.get() ?
                UberMinerBlockEntity::tick : null;
    }

    @Override
    public InteractionResult use(
            BlockState pState, Level level, BlockPos pPos, Player pPlayer,
            InteractionHand pHand, BlockHitResult pHit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pPos);
            if (blockEntity instanceof UberMinerBlockEntity) {
                pPlayer.openMenu((MenuProvider) blockEntity);
            }
            return InteractionResult.CONSUME;
        }
    }
}
