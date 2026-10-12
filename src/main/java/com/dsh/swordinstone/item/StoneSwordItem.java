package com.dsh.swordinstone.item;

import java.util.List;

import com.dsh.swordinstone.registry.ModDataComponents;
import com.dsh.swordinstone.registry.ModEffects;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * The Sword in Stone.
 *
 * <p>A mid/late game weapon that can be found early on if you are lucky enough to stumble across the
 * ruined tower it is stuck in.
 *
 * <ul>
 *   <li>Right-click and hold for 1.5 seconds to activate "Sanctuary".</li>
 *   <li>Sanctuary grants Resistance III, Strength II and Speed II for 10 minutes.</li>
 *   <li>12 minute cooldown between activations.</li>
 * </ul>
 */
public class StoneSwordItem extends SwordItem {
    /** 1.5 seconds of charging. */
    public static final int CHARGE_TICKS = 30;
    /** 10 minutes. */
    public static final int SANCTUARY_DURATION_TICKS = 20 * 60 * 10;
    /** 12 minutes. */
    public static final int COOLDOWN_TICKS = 20 * 60 * 12;

    public StoneSwordItem(Properties properties) {
        super(Tiers.DIAMOND, properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            if (!level.isClientSide) {
                SwordFeedback.actionBar(player,
                        Component.translatable("message.sword_in_stone.on_cooldown"));
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            stack.set(ModDataComponents.CHARGE_START.get(), level.getGameTime());
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide || !(living instanceof Player player)) {
            return;
        }
        long start = chargeStart(stack);
        if (start < 0L) {
            return;
        }
        long held = level.getGameTime() - start;

        if (held == CHARGE_TICKS) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.6F, 1.4F);
            SwordFeedback.actionBar(player,
                    Component.translatable("message.sword_in_stone.ready"));
        } else if (held > CHARGE_TICKS && held % 10 == 0) {
            // Periodic cue while the player keeps holding it charged.
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.35F, 1.6F);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        long start = chargeStart(stack);
        stack.remove(ModDataComponents.CHARGE_START.get());

        if (level.isClientSide || !(living instanceof Player player) || start < 0L) {
            return;
        }

        long held = level.getGameTime() - start;
        if (held < CHARGE_TICKS) {
            SwordFeedback.actionBar(player,
                    Component.translatable("message.sword_in_stone.too_early"));
            return;
        }
        castSanctuary(level, player);
    }

    /** Applies the buff, plays the effect and starts the cooldown. */
    public static void castSanctuary(Level level, Player player) {
        player.addEffect(new MobEffectInstance(ModEffects.resistance(), SANCTUARY_DURATION_TICKS, 2,
                false, true, true));
        player.addEffect(new MobEffectInstance(ModEffects.strength(), SANCTUARY_DURATION_TICKS, 1,
                false, true, true));
        player.addEffect(new MobEffectInstance(ModEffects.speed(), SANCTUARY_DURATION_TICKS, 1,
                false, true, true));

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.2F, 0.8F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.5F, 1.5F);

        player.getCooldowns().addCooldown(
                com.dsh.swordinstone.registry.ModItems.STONE_SWORD.get(), COOLDOWN_TICKS);

        SwordFeedback.actionBar(player,
                Component.translatable("message.sword_in_stone.cast"));
    }

    private static long chargeStart(ItemStack stack) {
        Long value = stack.get(ModDataComponents.CHARGE_START.get());
        return value == null ? -1L : value;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.sword_in_stone.skill").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.sword_in_stone.skill.resistance")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.sword_in_stone.skill.strength")
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.sword_in_stone.skill.speed")
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.sword_in_stone.skill.charge")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.sword_in_stone.skill.cooldown")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.sword_in_stone.insert")
                .withStyle(ChatFormatting.DARK_AQUA));
    }
}
