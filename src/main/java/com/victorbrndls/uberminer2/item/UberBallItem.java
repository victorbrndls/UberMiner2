package com.victorbrndls.uberminer2.item;

import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.entity.UberBallEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UberBallItem extends Item {

    private final int range;
    private final List<UberBallUpgrades> upgrades;

    public UberBallItem(int range, List<UberBallUpgrades> upgrades) {
        super(new Properties().stacksTo(32).tab(UberMiner.UBER_MINER_TAB));
        this.range = range;
        this.upgrades = upgrades;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            var thrownUberBall = new UberBallEntity(level, player, range, upgrades);
            thrownUberBall.setItem(itemstack);
            thrownUberBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownUberBall);
        }

        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack itemStack,
                                @Nullable Level level,
                                List<Component> components,
                                TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        var area = range * 2 + 1;
        components.add(new TextComponent(area + "x" + area).withStyle(ChatFormatting.WHITE));

        if (upgrades.contains(UberBallUpgrades.ADD_ITEMS_TO_PLAYER_INVENTORY)) {
            components.add(new TextComponent("Drops items near player").withStyle(ChatFormatting.YELLOW));
        } else {
            components.add(new TextComponent("Drops items on the ground"));
        }
    }
}
