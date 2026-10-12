package com.dsh.swordinstone.block;

import com.dsh.swordinstone.registry.ModItems;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The stone pedestal at the very top of the Sword-in-Stone tower.
 *
 * <p>While {@code HAS_SWORD} is true the sword is embedded in the stone and cannot simply be taken:
 * a player must right-click the pedestal while they are at least {@link #REQUIRED_LEVELS} experience
 * levels deep. Pulling the sword costs {@link #REQUIRED_LEVELS} levels and turns the pedestal into
 * an empty one. Once empty it acts as a normal decoration block.
 */
public class SwordPedestalBlock extends Block implements SimpleWaterloggedBlock {
    public static final MapCodec<SwordPedestalBlock> CODEC = simpleCodec(SwordPedestalBlock::new);

    public static final BooleanProperty HAS_SWORD = BooleanProperty.create("has_sword");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** Experience levels needed to pull the sword out of the stone. */
    public static final int REQUIRED_LEVELS = 10;

    // Matches the block model: two stone steps, a central column and a flared capital. The blade itself is
    // deliberately left out of the collision box so an inserted sword never blocks movement.
    private static final VoxelShape BASE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
    private static final VoxelShape COLUMN = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
    private static final VoxelShape SHAPE = java.util.stream.Stream.of(BASE, COLUMN)
            .reduce((a, b) -> net.minecraft.world.phys.shapes.Shapes.or(a, b))
            .orElse(BASE);

    public SwordPedestalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(HAS_SWORD, Boolean.TRUE)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends SwordPedestalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_SWORD, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                  CollisionContext context) {
        return SHAPE;
    }

    /** Right-click: pull the sword out, or push it back in. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, net.minecraft.world.InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (state.getValue(HAS_SWORD)) {
            return tryPull(level, pos, player);
        }
        if (stack.is(ModItems.STONE_SWORD.get())) {
            return reinsert(stack, level, pos, player);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!state.getValue(HAS_SWORD)) {
            // An empty pedestal is just decoration: right-clicking it with a bare hand does nothing.
            return InteractionResult.PASS;
        }
        tryPull(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    /** Put the sword back into the empty pedestal. */
    private static ItemInteractionResult reinsert(ItemStack stack, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        stack.shrink(1);
        level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(HAS_SWORD, true));
        level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.1F);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.6F, 1.3F);
        player.displayClientMessage(Component.translatable("message.sword_in_stone.inserted"), true);
        return ItemInteractionResult.SUCCESS;
    }

    private static ItemInteractionResult tryPull(Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (player.experienceLevel < REQUIRED_LEVELS) {
            player.displayClientMessage(Component.translatable(
                    "message.sword_in_stone.not_worthy", REQUIRED_LEVELS), true);
            level.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 1.0F, 0.6F);
            return ItemInteractionResult.CONSUME;
        }

        player.giveExperienceLevels(-REQUIRED_LEVELS);
        ItemStack sword = new ItemStack(ModItems.STONE_SWORD.get());
        if (!player.getInventory().add(sword)) {
            player.drop(sword, false);
        }

        level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(HAS_SWORD, false));
        level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.2F, 0.8F);
        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
        player.displayClientMessage(Component.translatable("message.sword_in_stone.pulled"), true);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(HAS_SWORD, Boolean.FALSE)
                .setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

}
