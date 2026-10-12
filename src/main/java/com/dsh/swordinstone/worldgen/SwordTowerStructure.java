package com.dsh.swordinstone.worldgen;

import java.util.Optional;

import com.dsh.swordinstone.registry.ModBlocks;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

/**
 * The ruined tower that holds the Sword in Stone.
 *
 * <p>The structure is generated entirely in code, so no binary {@code .nbt} template is needed. It is a
 * solid, tapering stone-brick tower with battlements on top and the sword pedestal at its highest point.
 * The whole thing is only 9x9 blocks wide, so the vertical <em>heightmap projection</em> used by the
 * jigsaw placer always lands on the tower's own roof and keeps a flat footprint.
 *
 * <p>Vertical layout, relative to the ground column the structure is placed on:
 * <pre>
 *   +8         pedestal (the sword)
 *   +7         3x3 crown floor + battlements
 *   +5 .. +6   3x3 crown
 *   +4         3x3 ceiling
 *   +1 .. +3   5x5 upper chamber (loot chest inside)
 *   -3 .. +0   9x9 body with a doorway
 *   -7 .. -4   9x9 foundation embedded in the ground
 * </pre>
 */
public class SwordTowerStructure extends Structure {
    public static final MapCodec<SwordTowerStructure> CODEC = simpleCodec(SwordTowerStructure::new);

    private static final int PEDESTAL_OFFSET = 8;
    private static final int CROWN_FLOOR_OFFSET = 7;
    private static final int CROWN_BOTTOM = 5;
    private static final int CEILING_OFFSET = 4;
    private static final int CHAMBER_BOTTOM = 1;
    private static final int CHAMBER_TOP = 3;
    private static final int BODY_BOTTOM = -3;
    private static final int BODY_TOP = 0;
    private static final int FOUNDATION_BOTTOM = -7;
    private static final int FOUNDATION_TOP = -4;

    public SwordTowerStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();

        // Never read a live heightmap here: during world generation the WORLD_SURFACE_WG heightmap of the
        // chunk being generated is not primed yet. The noise based query below is the supported entry point
        // (this is what vanilla's onTopOfChunkCenter / getLowestY helpers use).
        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

        LevelHeightAccessor heightAccessor = context.heightAccessor();
        int minY = heightAccessor.getMinBuildHeight() + 1;
        int maxY = heightAccessor.getMaxBuildHeight() - 1;

        // Ground level is offset 0, so the pedestal ends up on top of the crown and the foundation is
        // buried by terrain adaptation. Very low terrain (superflat worlds) simply gets a shorter, or even
        // an entirely clipped, foundation rather than no tower at all.
        int groundY = surfaceY;
        int bottomY = Math.max(groundY + FOUNDATION_BOTTOM, minY);
        int topY = groundY + PEDESTAL_OFFSET;
        if (topY > maxY) {
            return Optional.empty();
        }

        BoundingBox box = new BoundingBox(x - 4, bottomY, z - 4, x + 4, topY, z + 4);
        return Optional.of(new GenerationStub(new BlockPos(x, groundY, z),
                builder -> builder.addPiece(new TowerPiece(box, groundY))));
    }

    @Override
    public StructureType<?> type() {
        return com.dsh.swordinstone.registry.ModStructures.SWORD_TOWER.get();
    }

    /** The tower itself. */
    public static class TowerPiece extends StructurePiece {
        private final int groundWorldY;

        public TowerPiece(BoundingBox box, int groundWorldY) {
            super(com.dsh.swordinstone.registry.ModStructures.SWORD_TOWER_PIECE.get(), 0, box);
            this.groundWorldY = groundWorldY;
        }

        public TowerPiece(CompoundTag tag) {
            super(com.dsh.swordinstone.registry.ModStructures.SWORD_TOWER_PIECE.get(), tag);
            this.groundWorldY = tag.getInt("GroundY");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putInt("GroundY", this.groundWorldY);
        }

        private int layer(int offset) {
            return this.groundWorldY + offset;
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureManager structureManager,
                                ChunkGenerator generator, RandomSource random, BoundingBox chunkBox,
                                ChunkPos chunkPos, BlockPos pivot) {
            buildFoundation(level, random, chunkBox);
            buildBody(level, random, chunkBox);
            buildChamber(level, random, chunkBox);
            buildCeiling(level, random, chunkBox);
            buildCrown(level, random, chunkBox);
            decorate(level, random, chunkBox);
            placePedestal(level, chunkBox);
        }

        // ------------------------------------------------------------- layers

        private void buildFoundation(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
            for (int y = Math.max(layer(FOUNDATION_BOTTOM), this.boundingBox.minY());
                 y <= layer(FOUNDATION_TOP); y++) {
                fillSquare(level, random, chunkBox, 4, y, true);
            }
        }

        private void buildBody(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
            for (int y = BODY_BOTTOM; y <= BODY_TOP; y++) {
                fillSquare(level, random, chunkBox, 4, layer(y), false);
                if (y == BODY_BOTTOM || y == BODY_BOTTOM + 1) {
                    carve(level, chunkBox, 0, layer(y), 4);
                    carve(level, chunkBox, 0, layer(y), -4);
                }
                if (y == BODY_TOP - 1) {
                    carve(level, chunkBox, 4, layer(y), 0);
                    carve(level, chunkBox, -4, layer(y), 0);
                }
            }
        }

        private void buildChamber(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
            for (int y = CHAMBER_BOTTOM; y <= CHAMBER_TOP; y++) {
                ring(level, random, chunkBox, 2, layer(y));
            }
        }

        private void buildCeiling(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
            fillSquare(level, random, chunkBox, 2, layer(CEILING_OFFSET), false);
        }

        private void buildCrown(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
            for (int y = CROWN_BOTTOM; y <= CROWN_FLOOR_OFFSET - 1; y++) {
                ring(level, random, chunkBox, 1, layer(y));
            }
            fillSquare(level, random, chunkBox, 1, layer(CROWN_FLOOR_OFFSET), false);
            // Battlements: raise the four corners of the crown by one block.
            for (int cx : new int[] { -1, 1 }) {
                for (int cz : new int[] { -1, 1 }) {
                    setBlock(level, chunkBox, cx, layer(CROWN_FLOOR_OFFSET) + 1, cz,
                            Blocks.STONE_BRICK_WALL.defaultBlockState());
                }
            }
        }

        // ------------------------------------------------------------- detail

        private void decorate(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
            // A chest with supplies, sitting inside the upper chamber.
            int chestY = layer(CHAMBER_BOTTOM);
            createChest(level, chunkBox, random, x(-1), chestY, z(-1), BuiltInLootTables.SIMPLE_DUNGEON);

            // Chiselled trim flanking the doorway on the inside.
            for (int cz : new int[] { -2, 2 }) {
                setBlock(level, chunkBox, 3, layer(BODY_TOP), cz, Blocks.CHISELED_STONE_BRICKS.defaultBlockState());
                setBlock(level, chunkBox, -3, layer(BODY_TOP), cz, Blocks.CHISELED_STONE_BRICKS.defaultBlockState());
            }

            // Mossy patches scattered over the crown floor.
            for (int i = 0; i < 6; i++) {
                int dx = random.nextInt(3) - 1;
                int dz = random.nextInt(3) - 1;
                if (dx == 0 && dz == 0) {
                    continue;
                }
                setBlock(level, chunkBox, dx, layer(CROWN_FLOOR_OFFSET), dz,
                        Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
            }
        }

        private void placePedestal(WorldGenLevel level, BoundingBox chunkBox) {
            setBlock(level, chunkBox, 0, layer(PEDESTAL_OFFSET) - 1, 0,
                    Blocks.STONE_BRICKS.defaultBlockState());
            setBlock(level, chunkBox, 0, layer(PEDESTAL_OFFSET), 0,
                    ModBlocks.SWORD_PEDESTAL.get().defaultBlockState());
        }

        // ------------------------------------------------------------- helpers

        private static BlockState pickVariant(RandomSource random) {
            int roll = random.nextInt(100);
            if (roll < 55) {
                return Blocks.STONE_BRICKS.defaultBlockState();
            }
            if (roll < 75) {
                return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            }
            if (roll < 90) {
                return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            }
            return Blocks.STONE.defaultBlockState();
        }

        private void fillSquare(WorldGenLevel level, RandomSource random, BoundingBox chunkBox,
                                int radius, int y, boolean foundation) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockState state = foundation ? foundationBlock(random, dx, dz, radius)
                            : pickVariant(random);
                    setBlock(level, chunkBox, dx, y, dz, state);
                }
            }
        }

        private void ring(WorldGenLevel level, RandomSource random, BoundingBox chunkBox,
                          int radius, int y) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    setBlock(level, chunkBox, dx, y, dz, pickVariant(random));
                }
            }
        }

        /** Mostly plain stone inside so the buried part blends into terrain, bricks on the outside. */
        private BlockState foundationBlock(RandomSource random, int dx, int dz, int radius) {
            boolean edge = Math.abs(dx) == radius || Math.abs(dz) == radius;
            if (edge) {
                return pickVariant(random);
            }
            return random.nextInt(4) == 0 ? Blocks.STONE_BRICKS.defaultBlockState()
                    : Blocks.STONE.defaultBlockState();
        }

        private void carve(WorldGenLevel level, BoundingBox chunkBox, int dx, int y, int dz) {
            setBlock(level, chunkBox, dx, y, dz, Blocks.CAVE_AIR.defaultBlockState());
        }

        private int x(int dx) {
            return this.boundingBox.minX() + 4 + dx;
        }

        private int z(int dz) {
            return this.boundingBox.minZ() + 4 + dz;
        }

        private void setBlock(WorldGenLevel level, BoundingBox chunkBox, int dx, int y, int dz,
                              BlockState state) {
            BlockPos pos = new BlockPos(x(dx), y, z(dz));
            if (!chunkBox.isInside(pos)) {
                return;
            }
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }
    }
}
