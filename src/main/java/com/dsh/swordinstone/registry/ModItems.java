package com.dsh.swordinstone.registry;

import com.dsh.swordinstone.SwordInStone;
import com.dsh.swordinstone.item.StoneSwordItem;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SwordInStone.MODID);

    /**
     * The Sword in Stone: a diamond-grade sword with a bit of extra punch, 1800 durability and
     * fire resistance so a lava accident does not cost you the run.
     */
    public static final DeferredItem<StoneSwordItem> STONE_SWORD = ITEMS.register("stone_sword",
            () -> new StoneSwordItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .fireResistant()
                    .stacksTo(1)
                    .durability(1800)
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 7.0D,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.4D,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModItems::addToCreativeTabs);
    }

    private static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(STONE_SWORD);
            event.accept(ModBlocks.PEDESTAL_ITEM);
        }
    }

    private ModItems() {
    }
}
