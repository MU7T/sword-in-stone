package com.dsh.swordinstone;

import org.slf4j.Logger;

import com.dsh.swordinstone.registry.ModBlocks;
import com.dsh.swordinstone.registry.ModDataComponents;
import com.dsh.swordinstone.registry.ModEffects;
import com.dsh.swordinstone.registry.ModItems;
import com.dsh.swordinstone.registry.ModStructures;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SwordInStone.MODID)
public class SwordInStone {
    public static final String MODID = "sword_in_stone";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SwordInStone(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.register(modEventBus);
        ModEffects.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModStructures.register(modEventBus);
    }
}
