package com.victorbrndls.uberminer2.item;

import static com.victorbrndls.uberminer2.util.OreUtil.isOre;

import com.mojang.datafixers.util.Pair;
import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.util.ItemStackUtil;
import com.victorbrndls.uberminer2.util.LootContextUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreAttractorItem extends Item {

    public static String TAG_ELEMENT = "Properties";
    public static String TAG_ELEMENT_TIER = "Tier";

    @Nullable
    private OreAttractorTier tier;

    private int operationTime = 0;
    private boolean isActive = false;

    public OreAttractorItem() {
        super(new Item.Properties()
                      .durability(1)
                      .setNoRepair()
                      .tab(UberMiner.UBER_MINER_TAB)
        );
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getTier(stack).durability;
    }

    private OreAttractorTier getTier(ItemStack itemStack) {
        if (tier == null) {
            CompoundTag tag = itemStack.getOrCreateTagElement(TAG_ELEMENT);

            final var tierName = tag.getString(TAG_ELEMENT_TIER);
            try {
                OreAttractorTier.valueOf(tierName);
            } catch (IllegalArgumentException | NullPointerException e) {
                UberMiner.LOGGER.error("Couldn't read tier, name: {}", tierName, e);
                tier = OreAttractorTier.TIER_I;
            }
        }

        return tier;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide)
            isActive = !isActive;

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!isActive) return;

        operationTime++;
        if (operationTime < getTier(itemStack).operationTime) return;
        operationTime = 0;

        final var mined = mineOre(entity, itemStack);

        if (mined && entity instanceof Player player) {
            if (!player.getAbilities().instabuild)
                itemStack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }

    /**
     * @return true if ore was mined
     */
    private boolean mineOre(Entity entity, ItemStack itemStack) {
        Level level = entity.level;
        final var ore = findOre(entity.level, entity.blockPosition(), getTier(itemStack).radius);
        if (ore == null) return false;

        BlockPos orePos = ore.getFirst();
        BlockState oreState = ore.getSecond();
        UberMiner.LOGGER.debug("Mining {}", oreState.getBlock().getRegistryName());

        level.setBlock(orePos, Blocks.STONE.defaultBlockState(), 3);

        dropOre(entity, level, orePos, oreState);

        return true;
    }

    @Nullable
    private Pair<BlockPos, BlockState> findOre(Level level, BlockPos position, int radius) {
        final var distance = radius <= 1 ? 1 : radius - 1;

        final var blocksToScan = BlockPos.betweenClosed(
                position.getX() - distance,
                position.getY() - distance,
                position.getZ() - distance,
                position.getX() + distance,
                position.getY() + distance,
                position.getZ() + distance
        );

        for (BlockPos blockPos : blocksToScan) {
            final var block = level.getBlockState(blockPos);
            if (isOre(block)) return Pair.of(blockPos, block);
        }

        return null;
    }

    private void dropOre(Entity entity, Level level, BlockPos orePos, BlockState oreState) {
        LootContext.Builder lootContextBuilder = LootContextUtil.getLootContextBuilder(level, orePos);
        final var drops = oreState.getDrops(lootContextBuilder);

        var spawnPos = entity.blockPosition();
        ItemStackUtil.drop(level, spawnPos, drops);
    }

    @Override
    public boolean isEnchantable(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return isActive;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            @Nullable Level level,
            List<Component> components,
            TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        components.add(
                new TextComponent(getTier(itemStack).radius + "x" + getTier(itemStack).radius)
                        .withStyle(ChatFormatting.BLUE));
        components.add(
                new TextComponent("Right click to enable/disable")
                        .withStyle(ChatFormatting.WHITE));
    }

}
