package com.dsh.swordinstone.registry;

import com.dsh.swordinstone.SwordInStone;
import com.dsh.swordinstone.block.SwordPedestalBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SwordInStone.MODID);

    /** The pedestal the sword sits in, at the top of the tower. */
    public static final DeferredBlock<SwordPedestalBlock> SWORD_PEDESTAL = BLOCKS.register(
            "sword_pedestal",
            () -> new SwordPedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.BLOCK)
                    .noOcclusion()));

    public static final DeferredItem<BlockItem> PEDESTAL_ITEM = ModItems.ITEMS.registerSimpleBlockItem(
            "sword_pedestal", SWORD_PEDESTAL);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    private ModBlocks() {
    }
}
