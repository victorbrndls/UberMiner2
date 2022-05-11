package com.victorbrndls.uberminer2.item;

import com.victorbrndls.uberminer2.UberMiner;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OreAttractorItem extends Item {

    private OreAttractorTier tier;

    private int operationTime = 0;
    private boolean isActive = false;

    public OreAttractorItem() {
        this(OreAttractorTier.TIER_I);
    }

    public OreAttractorItem(OreAttractorTier tier) {
        super(new Item.Properties()
                      .durability(tier.durability)
                      .setNoRepair()
                      .tab(UberMiner.UBER_MINER_TAB)
        );
        this.tier = tier;
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
        if (operationTime < tier.operationTime) return;
        operationTime = 0;

        UberMiner.LOGGER.debug("Mining");

        if (entity instanceof Player player) {
            if (!player.getAbilities().instabuild)
                itemStack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return isActive;
    }
}
