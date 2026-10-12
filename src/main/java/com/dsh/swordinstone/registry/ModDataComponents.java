package com.dsh.swordinstone.registry;

import com.dsh.swordinstone.SwordInStone;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Custom item data components.
 *
 * <p>{@code CHARGE_START} stores the {@code Level.getGameTime()} value of the moment the player started
 * charging the Sword in Stone. Keeping it on the stack means it is saved, synced and cleared for free.
 * A value of {@code -1} means "not charging".
 */
public final class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SwordInStone.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> CHARGE_START =
            COMPONENTS.register("charge_start", () -> DataComponentType.<Long>builder()
                    .persistent(com.mojang.serialization.Codec.LONG)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_LONG)
                    .build());

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }

    private ModDataComponents() {
    }
}
