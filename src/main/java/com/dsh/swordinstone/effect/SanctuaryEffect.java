package com.dsh.swordinstone.effect;

import com.dsh.swordinstone.SwordInStone;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * A beneficial effect whose attribute modifiers are carried by the effect itself.
 *
 * <p><b>Why the modifiers live here:</b> registering them through NeoForge's
 * {@code EntityAttributeModificationEvent} attaches them to every player <i>permanently</i>, which made
 * the Sanctuary buffs apply from the moment the mod loaded — completely disconnected from the sword
 * skill. Modifiers added via {@link MobEffect#addAttributeModifier} are applied when the effect starts
 * and removed when it ends, which is the behaviour we want.
 *
 * <p>Vanilla scales a modifier as {@code amount * (amplifier + 1)}, so one registration already gives
 * Resistance III = three steps of knockback resistance, Strength II = two steps of attack damage, etc.
 *
 * <p>{@link #isBeneficial()} deliberately returns {@code false}: vanilla groups effects by that flag and
 * would otherwise tint this mod's full-colour icon.
 */
public class SanctuaryEffect extends MobEffect {

    public SanctuaryEffect(int color) {
        super(MobEffectCategory.BENEFICIAL, color);
    }

    /**
     * Registers one attribute modifier and returns {@code this} so calls can be chained.
     *
     * @param attribute the attribute to modify
     * @param path      modifier path; namespaced under this mod automatically
     * @param amount    amount per amplifier step
     * @param operation how the amount is applied
     */
    public SanctuaryEffect withAttribute(Holder<Attribute> attribute, String path, double amount,
                                         AttributeModifier.Operation operation) {
        this.addAttributeModifier(attribute,
                ResourceLocation.fromNamespaceAndPath(SwordInStone.MODID, path),
                amount, operation);
        return this;
    }

    /** +N of the attribute per amplifier step. */
    public SanctuaryEffect withAdd(Holder<Attribute> attribute, String path, double amount) {
        return withAttribute(attribute, path, amount, AttributeModifier.Operation.ADD_VALUE);
    }

    /** +N% of the attribute's total per amplifier step. */
    public SanctuaryEffect withMultiply(Holder<Attribute> attribute, String path, double fraction) {
        return withAttribute(attribute, path, fraction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
