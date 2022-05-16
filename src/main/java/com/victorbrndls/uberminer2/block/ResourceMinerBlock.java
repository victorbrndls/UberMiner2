package com.victorbrndls.uberminer2.block;

import com.victorbrndls.uberminer2.entity.ResourceMinerBlockEntity;
import com.victorbrndls.uberminer2.registry.UberMinerBlockEntities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResourceMinerBlock extends BaseEntityBlock {

    public ResourceMinerBlock() {
        super(Properties.of(Material.DECORATION).strength(0.5F).sound(SoundType.METAL));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return UberMinerBlockEntities.RESOURCE_MINER.get().create(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState blockState,
            BlockEntityType<T> entityType) {
        return level.isClientSide ? null : entityType == UberMinerBlockEntities.RESOURCE_MINER.get() ?
                ResourceMinerBlockEntity::tick : null;
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ResourceMinerBlockEntity) {
                NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) blockEntity, blockEntity.getBlockPos());
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack, @Nullable BlockGetter level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);

        components.add(new TextComponent("Mines all blocks excepts ores")
                               .withStyle(ChatFormatting.WHITE));
        components.add(new TextComponent("Blocks are voided")
                               .withStyle(ChatFormatting.WHITE));

    }

}
