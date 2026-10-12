package com.dsh.swordinstone.client;

import com.dsh.swordinstone.SwordInStone;
import com.dsh.swordinstone.registry.ModEffects;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * Draws this mod's mob effect icons straight from their own PNGs, bypassing the
 * {@code minecraft:mob_effects} texture atlas.
 *
 * <p>Atlas sprites are normally looked up as {@code textures/mob_effect/<namespace>/<path>.png}. That
 * discovery did not pick these textures up, so the icons are drawn explicitly instead. Drawing them here
 * also preserves the artwork's exact colours rather than letting the beneficial-effect tint apply.
 */
@EventBusSubscriber(modid = SwordInStone.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SanctuaryClientEffects {
    private static final int ICON_SIZE = 18;

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IconRenderer(icon("sanctuary_resistance")),
                ModEffects.SANCTUARY_RESISTANCE);
        event.registerMobEffect(new IconRenderer(icon("sanctuary_strength")),
                ModEffects.SANCTUARY_STRENGTH);
        event.registerMobEffect(new IconRenderer(icon("sanctuary_speed")),
                ModEffects.SANCTUARY_SPEED);
    }

    private static ResourceLocation icon(String name) {
        return ResourceLocation.fromNamespaceAndPath(SwordInStone.MODID,
                "textures/mob_effect/" + SwordInStone.MODID + "/" + name + ".png");
    }

    /** Blits one 18x18 icon out of its own texture sheet. */
    private record IconRenderer(ResourceLocation icon) implements IClientMobEffectExtensions {
        @Override
        public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics,
                                     int x, int y, float z, float alpha) {
            graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
            graphics.blit(icon, x + 3, y + 3, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            return true;
        }

        @Override
        public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen,
                                           GuiGraphics graphics, int x, int y, int blitOffset) {
            graphics.blit(icon, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            return true;
        }
    }

    private SanctuaryClientEffects() {
    }
}
