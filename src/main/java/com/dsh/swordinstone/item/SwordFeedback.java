package com.dsh.swordinstone.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Small helper for the action-bar messages used by the mod. */
public final class SwordFeedback {
    public static void actionBar(Player player, Component message) {
        player.displayClientMessage(message, true);
    }

    private SwordFeedback() {
    }
}
