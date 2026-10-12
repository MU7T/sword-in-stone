package com.dsh.swordinstone.registry;

import com.dsh.swordinstone.SwordInStone;
import com.dsh.swordinstone.effect.SanctuaryEffect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The three buffs granted by the Excalibur's "Sanctuary" skill.
 *
 * <p>In game these read as the vanilla potion tiers:
 * <ul>
 *   <li>Resistance III (amplifier 2) — from the sword skill only</li>
 *   <li>Strength II (amplifier 1) — from the sword skill only</li>
 *   <li>Speed II (amplifier 1) — from the sword skill only</li>
 * </ul>
 *
 * <p>The attribute modifiers are attached to each effect via
 * {@link SanctuaryEffect#withAdd}/{@link SanctuaryEffect#withMultiply}, so they only apply while the
 * effect is active. They are deliberately <b>not</b> registered through
 * {@code EntityAttributeModificationEvent}: that would add them to every player permanently, which made
 * the buffs take effect on world join instead of on casting the skill.
 *
 * <p>The per-step amounts are copied from the vanilla potions so a tier behaves identically:
 * Resistance gives +0.3 knockback resistance per step, Strength +3 attack damage per step and Speed
 * +20% movement speed per step.
 */
public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, SwordInStone.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> SANCTUARY_RESISTANCE =
            EFFECTS.register("sanctuary_resistance",
                    () -> new SanctuaryEffect(0x2E5A88)
                            .withAdd(Attributes.KNOCKBACK_RESISTANCE, "sanctuary_knockback", 0.3D));

    public static final DeferredHolder<MobEffect, MobEffect> SANCTUARY_STRENGTH =
            EFFECTS.register("sanctuary_strength",
                    () -> new SanctuaryEffect(0x93241F)
                            .withAdd(Attributes.ATTACK_DAMAGE, "sanctuary_attack", 3.0D));

    public static final DeferredHolder<MobEffect, MobEffect> SANCTUARY_SPEED =
            EFFECTS.register("sanctuary_speed",
                    () -> new SanctuaryEffect(0x33C7D6)
                            .withMultiply(Attributes.MOVEMENT_SPEED, "sanctuary_speed", 0.2D));

    /** Called from the mod constructor. */
    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }

    /** Convenience: the raw {@link Holder}s used when applying the buff. */
    public static Holder<MobEffect> resistance() {
        return SANCTUARY_RESISTANCE;
    }

    public static Holder<MobEffect> strength() {
        return SANCTUARY_STRENGTH;
    }

    public static Holder<MobEffect> speed() {
        return SANCTUARY_SPEED;
    }

    private ModEffects() {
    }
}
