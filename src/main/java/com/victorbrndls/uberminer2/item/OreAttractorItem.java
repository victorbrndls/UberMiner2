package com.victorbrndls.uberminer2.item;

import static com.victorbrndls.uberminer2.util.OreUtil.isOre;

import com.mojang.datafixers.util.Pair;
import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.registry.UberMinerItems;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreAttractorItem extends Item {

    public static String TAG_ELEMENT = "Properties";
    public static String TAG_ELEMENT_TIER = "Tier";
    public static String TAG_ELEMENT_ACTIVE = "Active";
    public static String TAG_ELEMENT_OPERATION_TICK = "OperationTick";

    public OreAttractorItem() {
        super(new Item.Properties()
                      .durability(1)
                      .setNoRepair()
                      .tab(UberMiner.UBER_MINER_TAB)
        );
    }

    @NotNull
    private OreAttractorTier getTier(ItemStack itemStack) {
        final var tier = getTierOrNull(itemStack);
        if (tier != null) return tier;

        return OreAttractorTier.TIER_I;
    }

    public static OreAttractorTier getTierOrNull(ItemStack itemStack) {
        final var tierName = getTag(itemStack).getString(TAG_ELEMENT_TIER);

        try {
            return OreAttractorTier.valueOf(tierName);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        if (!getTag(itemStack).contains(TAG_ELEMENT_TIER)) return 1;
        return getTier(itemStack).durability;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide)
            toggleActive(itemStack);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!isActive(itemStack)) return;
        final var server = level.getServer();

        if (!isReadyForOperation(itemStack, server.getTickCount())) return;
        setNextOperationTick(itemStack, server.getTickCount());

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
        return isActive(itemStack);
    }

    private static CompoundTag getTag(ItemStack itemStack) {
        return itemStack.getOrCreateTagElement(TAG_ELEMENT);
    }

    private void setActive(ItemStack itemStack, boolean isActive) {
        getTag(itemStack).putBoolean(TAG_ELEMENT_ACTIVE, isActive);
    }

    private boolean isActive(ItemStack itemStack) {
        return getTag(itemStack).getBoolean(TAG_ELEMENT_ACTIVE);
    }

    private void toggleActive(ItemStack itemStack) {
        setActive(itemStack, !isActive(itemStack));
    }

    private void setOperationTick(ItemStack itemStack, long tick) {
        getTag(itemStack).putLong(TAG_ELEMENT_OPERATION_TICK, tick);
    }

    private long getOperationTick(ItemStack itemStack) {
        return getTag(itemStack).getLong(TAG_ELEMENT_OPERATION_TICK);
    }

    private void setNextOperationTick(ItemStack itemStack, long serverTick) {
        final var operationTime = getTier(itemStack).operationTime;
        setOperationTick(itemStack, serverTick + operationTime);
    }

    private boolean isReadyForOperation(ItemStack itemStack, long serverTick) {
        final var nextTick = getOperationTick(itemStack);
        if (nextTick == 0L) setNextOperationTick(itemStack, serverTick);

        return serverTick > nextTick;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            @Nullable Level level,
            List<Component> components,
            TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        if (getTag(itemStack).contains(TAG_ELEMENT_TIER)) {
            components.add(
                    new TextComponent(getTier(itemStack).radius + "x" + getTier(itemStack).radius)
                            .withStyle(ChatFormatting.BLUE));
        }

        components.add(
                new TextComponent("Right click to enable/disable")
                        .withStyle(ChatFormatting.WHITE));
    }

    public static ItemStack createFromTier(OreAttractorTier tier) {
        ItemStack itemStack = new ItemStack(UberMinerItems.ORE_ATTRACTOR.get(), 1);
        CompoundTag tag = itemStack.getOrCreateTagElement(TAG_ELEMENT);

        tag.putString(TAG_ELEMENT_TIER, tier.name());
        
        return itemStack;
    }

}
