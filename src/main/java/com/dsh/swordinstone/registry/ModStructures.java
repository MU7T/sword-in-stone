package com.dsh.swordinstone.registry;

import com.dsh.swordinstone.SwordInStone;
import com.dsh.swordinstone.worldgen.SwordTowerStructure;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, SwordInStone.MODID);

    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, SwordInStone.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<SwordTowerStructure>> SWORD_TOWER =
            STRUCTURE_TYPES.register("sword_tower", () -> () -> SwordTowerStructure.CODEC);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> SWORD_TOWER_PIECE =
            PIECE_TYPES.register("sword_tower_piece", () -> {
                StructurePieceType.ContextlessType type = tag -> new SwordTowerStructure.TowerPiece(tag);
                return type;
            });

    public static void register(IEventBus modEventBus) {
        STRUCTURE_TYPES.register(modEventBus);
        PIECE_TYPES.register(modEventBus);
    }

    private ModStructures() {
    }
}
