package org.fiddlemc.fiddle.impl.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;

/**
 * A 1/8th cube.
 */
public class BevelBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<BevelBlock> CODEC = simpleCodec(BevelBlock::new);
    public static final BooleanProperty NORTH_WEST_BOTTOM = FiddleBlockStateProperties.NORTH_WEST_BOTTOM;
    public static final BooleanProperty SOUTH_WEST_BOTTOM = FiddleBlockStateProperties.SOUTH_WEST_BOTTOM;
    public static final BooleanProperty NORTH_WEST_TOP = FiddleBlockStateProperties.NORTH_WEST_TOP;
    public static final BooleanProperty SOUTH_WEST_TOP = FiddleBlockStateProperties.SOUTH_WEST_TOP;
    public static final BooleanProperty NORTH_EAST_BOTTOM = FiddleBlockStateProperties.NORTH_EAST_BOTTOM;
    public static final BooleanProperty SOUTH_EAST_BOTTOM = FiddleBlockStateProperties.SOUTH_EAST_BOTTOM;
    public static final BooleanProperty NORTH_EAST_TOP = FiddleBlockStateProperties.NORTH_EAST_TOP;
    public static final BooleanProperty SOUTH_EAST_TOP = FiddleBlockStateProperties.SOUTH_EAST_TOP;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * An array of {@link VoxelShape} that can be efficiently queried by a bitset index,
     * with the bits indicating:
     * <ul>
     *     <li>1 ({@code 1 << 0}): {@link #NORTH_WEST_BOTTOM}</li>
     *     <li>2 ({@code 1 << 1}): {@link #SOUTH_WEST_BOTTOM}</li>
     *     <li>4 ({@code 1 << 2}): {@link #NORTH_WEST_TOP}</li>
     *     <li>8 ({@code 1 << 3}): {@link #SOUTH_WEST_TOP}</li>
     *     <li>16 ({@code 1 << 4}): {@link #NORTH_EAST_BOTTOM}</li>
     *     <li>32 ({@code 1 << 5}): {@link #SOUTH_EAST_BOTTOM}</li>
     *     <li>64 ({@code 1 << 6}): {@link #NORTH_EAST_TOP}</li>
     *     <li>128 ({@code 1 << 7}): {@link #SOUTH_EAST_TOP}</li>
     * </ul>
     */
    private static final VoxelShape[] SHAPES;

    private static boolean areAllEmpty(boolean[][][] filled) {
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    if (filled[x][y][z]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean areAllFilled(boolean[][][] filled, int[] minSidePerDimension, int[] maxSidePerDimension) {
        for (int x = minSidePerDimension[0]; x <= maxSidePerDimension[0]; x++) {
            for (int y = minSidePerDimension[1]; y <= maxSidePerDimension[1]; y++) {
                for (int z = minSidePerDimension[2]; z <= maxSidePerDimension[2]; z++) {
                    if (!filled[x][y][z]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void zeroOut(boolean[][][] filled, int[] minSidePerDimension, int[] maxSidePerDimension) {
        for (int x = minSidePerDimension[0]; x <= maxSidePerDimension[0]; x++) {
            for (int y = minSidePerDimension[1]; y <= maxSidePerDimension[1]; y++) {
                for (int z = minSidePerDimension[2]; z <= maxSidePerDimension[2]; z++) {
                    filled[x][y][z] = false;
                }
            }
        }
    }

    private static @Nullable VoxelShape getVoxelShapeIfNotEmpty(int arrayI) {
        // For an empty block
        if (arrayI == 0) {
            return null;
        }
        // For a full block
        if (arrayI == (1 << 8) - 1) {
            return Block.box(0, 0, 0, 16, 16, 16);
        }
        // For a non-uniform block, construct a convenient 3D array of filled corners
        boolean[][][] filled = new boolean[2][2][2];
        filled[0][0][0] = (arrayI & (1 << 0)) != 0;
        filled[0][0][1] = (arrayI & (1 << 1)) != 0;
        filled[0][1][0] = (arrayI & (1 << 2)) != 0;
        filled[0][1][1] = (arrayI & (1 << 3)) != 0;
        filled[1][0][0] = (arrayI & (1 << 4)) != 0;
        filled[1][0][1] = (arrayI & (1 << 5)) != 0;
        filled[1][1][0] = (arrayI & (1 << 6)) != 0;
        filled[1][1][1] = (arrayI & (1 << 7)) != 0;
        return getDetailedVoxelShapeIfNotEmpty(filled, false);
    }

    private static @Nullable VoxelShape getDetailedVoxelShapeIfNotEmpty(boolean[][][] filled, boolean skipHalf) {
        // For an empty block
        if (areAllEmpty(filled)) {
            return null;
        }
        // Check if there is any full half block, then union it with the other half
        if (!skipHalf) {
            int[] minSidePerDimension = new int[3];
            int[] maxSidePerDimension = new int[3];
            Arrays.fill(maxSidePerDimension, 1);
            for (int dimension = 0; dimension < 3; dimension++) {
                for (int side = 0; side <= 1; side++) {
                    minSidePerDimension[dimension] = side;
                    maxSidePerDimension[dimension] = side;
                    if (areAllFilled(filled, minSidePerDimension, maxSidePerDimension)) {
                        // Zero out the used states
                        zeroOut(filled, minSidePerDimension, maxSidePerDimension);
                        VoxelShape fullPartShape = Block.box(minSidePerDimension[0] * 8, minSidePerDimension[1] * 8, minSidePerDimension[2] * 8, (maxSidePerDimension[0] + 1) * 8, (maxSidePerDimension[1] + 1) * 8, (maxSidePerDimension[2] + 1) * 8);
                        @Nullable VoxelShape otherShape = getDetailedVoxelShapeIfNotEmpty(filled, true);
                        return otherShape != null ? Shapes.or(fullPartShape, otherShape) : fullPartShape;
                    }
                }
                minSidePerDimension[dimension] = 0;
                maxSidePerDimension[dimension] = 1;
            }
        }
        // Check if there is any full smaller cuboid, then union it with the rest
        int[] minSidePerDimension = new int[3];
        int[] maxSidePerDimension = new int[3];
        Arrays.fill(maxSidePerDimension, 1);
        for (int freeDimension = 0; freeDimension < 3; freeDimension++) {
            int dimension1 = (freeDimension + 1) % 3;
            int dimension2 = (freeDimension + 2) % 3;
            for (int side1 = 0; side1 <= 1; side1++) {
                minSidePerDimension[dimension1] = side1;
                maxSidePerDimension[dimension1] = side1;
                for (int side2 = 0; side2 <= 1; side2++) {
                    minSidePerDimension[dimension2] = side2;
                    maxSidePerDimension[dimension2] = side2;
                    if (areAllFilled(filled, minSidePerDimension, maxSidePerDimension)) {
                        // Zero out the used states
                        zeroOut(filled, minSidePerDimension, maxSidePerDimension);
                        VoxelShape fullPartShape = Block.box(minSidePerDimension[0] * 8, minSidePerDimension[1] * 8, minSidePerDimension[2] * 8, (maxSidePerDimension[0] + 1) * 8, (maxSidePerDimension[1] + 1) * 8, (maxSidePerDimension[2] + 1) * 8);
                        @Nullable VoxelShape otherShape = getDetailedVoxelShapeIfNotEmpty(filled, true);
                        return otherShape != null ? Shapes.or(fullPartShape, otherShape) : fullPartShape;
                    }
                    minSidePerDimension[dimension2] = 0;
                    maxSidePerDimension[dimension2] = 1;
                }
                minSidePerDimension[dimension1] = 0;
                maxSidePerDimension[dimension1] = 1;
            }
        }
        // Check for any corner, then union it with the rest
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    if (filled[x][y][z]) {
                        filled[x][y][z] = false;
                        VoxelShape fullPartShape = Block.box(x * 8, y * 8, z * 8, (x + 1) * 8, (y + 1) * 8, (z + 1) * 8);
                        @Nullable VoxelShape otherShape = getDetailedVoxelShapeIfNotEmpty(filled, true);
                        return otherShape != null ? Shapes.or(fullPartShape, otherShape) : fullPartShape;
                    }
                }
            }
        }
        // Can't end up here: empty state was checked at the start
        throw new IllegalStateException();
    }

    private static VoxelShape getVoxelShape(int arrayI) {
        @Nullable VoxelShape shape = getVoxelShapeIfNotEmpty(arrayI);
        return shape != null ? shape : Shapes.empty();
    }

    static {
        SHAPES = new VoxelShape[1 << 8];
        for (int arrayI = 0; arrayI < SHAPES.length; arrayI++) {
            SHAPES[arrayI] = getVoxelShape(arrayI);
        }
    }

    public BevelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(NORTH_WEST_BOTTOM, true)
                .setValue(SOUTH_WEST_BOTTOM, false)
                .setValue(NORTH_WEST_TOP, false)
                .setValue(SOUTH_WEST_TOP, false)
                .setValue(NORTH_EAST_BOTTOM, false)
                .setValue(SOUTH_EAST_BOTTOM, false)
                .setValue(NORTH_EAST_TOP, false)
                .setValue(SOUTH_EAST_TOP, false)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    public MapCodec<? extends BevelBlock> codec() {
        return CODEC;
    }

    /**
     * @see SlabBlock#useShapeForLightOcclusion
     */
    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return !isFull(state);
    }

    /**
     * @see SlabBlock#createBlockStateDefinition
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            NORTH_WEST_BOTTOM,
            SOUTH_WEST_BOTTOM,
            NORTH_WEST_TOP,
            SOUTH_WEST_TOP,
            NORTH_EAST_BOTTOM,
            SOUTH_EAST_BOTTOM,
            NORTH_EAST_TOP,
            SOUTH_EAST_TOP,
            WATERLOGGED
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[(state.getValue(NORTH_WEST_BOTTOM) ? (1 << 0) : 0) +
            (state.getValue(SOUTH_WEST_BOTTOM) ? (1 << 1) : 0) +
            (state.getValue(NORTH_WEST_TOP) ? (1 << 2) : 0) +
            (state.getValue(SOUTH_WEST_TOP) ? (1 << 3) : 0) +
            (state.getValue(NORTH_EAST_BOTTOM) ? (1 << 4) : 0) +
            (state.getValue(SOUTH_EAST_BOTTOM) ? (1 << 5) : 0) +
            (state.getValue(NORTH_EAST_TOP) ? (1 << 6) : 0) +
            (state.getValue(SOUTH_EAST_TOP) ? (1 << 7) : 0)];
    }

    private static BooleanProperty getTargetProperty(BlockPlaceContext context, BlockPos pos) {
        Vec3 hit = context.getClickLocation();
        Direction face = context.getClickedFace();

        // Push the hit slightly inside the block to avoid boundary ambiguity
        double epsilon = 1e-6;
        double localX = hit.x - pos.getX() + face.getStepX() * epsilon;
        double localY = hit.y - pos.getY() + face.getStepY() * epsilon;
        double localZ = hit.z - pos.getZ() + face.getStepZ() * epsilon;

        int x = localX < 0.5 ? 0 : 1;
        int y = localY < 0.5 ? 0 : 1;
        int z = localZ < 0.5 ? 0 : 1;

        return switch ((x << 2) | (y << 1) | z) {
            case 0b000 -> NORTH_WEST_BOTTOM;
            case 0b001 -> SOUTH_WEST_BOTTOM;
            case 0b010 -> NORTH_WEST_TOP;
            case 0b011 -> SOUTH_WEST_TOP;
            case 0b100 -> NORTH_EAST_BOTTOM;
            case 0b101 -> SOUTH_EAST_BOTTOM;
            case 0b110 -> NORTH_EAST_TOP;
            case 0b111 -> SOUTH_EAST_TOP;
            default -> throw new IllegalStateException();
        };
    }
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();

        BlockState stateAtPos = level.getBlockState(pos);

        // CASE 1: clicking an existing bevel block → always try to fill it
        if (stateAtPos.is(this)) {
            BooleanProperty prop = getTargetProperty(context, pos);
            if (!stateAtPos.getValue(prop)) {
                return stateAtPos.setValue(prop, true);
            }
            return null;
        }

        // CASE 2: normal placement logic (like slabs)
        BlockPos placePos;
        if (stateAtPos.canBeReplaced(context)) {
            placePos = pos;
        } else {
            placePos = pos.relative(face);
        }

        BlockState existing = level.getBlockState(placePos);

        // If placing into another bevel → merge
        if (existing.is(this)) {
            BooleanProperty prop = getTargetProperty(context, placePos);
            if (!existing.getValue(prop)) {
                return existing.setValue(prop, true);
            }
            return null;
        }

        // Otherwise create new bevel block
        FluidState fluid = level.getFluidState(placePos);

        BooleanProperty prop = getTargetProperty(context, placePos);

        return this.defaultBlockState()
            .setValue(NORTH_WEST_BOTTOM, false)
            .setValue(SOUTH_WEST_BOTTOM, false)
            .setValue(NORTH_WEST_TOP, false)
            .setValue(SOUTH_WEST_TOP, false)
            .setValue(NORTH_EAST_BOTTOM, false)
            .setValue(SOUTH_EAST_BOTTOM, false)
            .setValue(NORTH_EAST_TOP, false)
            .setValue(SOUTH_EAST_TOP, false)
            .setValue(prop, true)
            .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();

        BlockState stateAtPos = context.getLevel().getBlockState(pos);

        // If this block isn't actually the target, don't allow replacement
        if (!stateAtPos.is(this)) {
            return false;
        }

        BooleanProperty prop = getTargetProperty(context, pos);
        return !state.getValue(prop);
    }

    /**
     * @see SlabBlock#getFluidState
     */
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    /**
     * @see SlabBlock#placeLiquid
     */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return !isFull(state) && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    /**
     * @see SlabBlock#canPlaceLiquid
     */
    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity owner, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !isFull(state) && SimpleWaterloggedBlock.super.canPlaceLiquid(owner, level, pos, state, fluid);
    }

    /**
     * @see SlabBlock#updateShape
     */
    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess scheduledTickAccess,
        BlockPos pos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    /**
     * @see SlabBlock#isPathfindable
     */
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        switch (pathComputationType) {
            case LAND:
                return false;
            case WATER:
                return state.getFluidState().is(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    /**
     * @return Whether every 1/8th cube of the given block state is filled.
     */
    public static boolean isFull(BlockState state) {
        return state.getValue(NORTH_WEST_BOTTOM) &&
            state.getValue(SOUTH_WEST_BOTTOM) &&
            state.getValue(NORTH_WEST_TOP) &&
            state.getValue(SOUTH_WEST_TOP) &&
            state.getValue(NORTH_EAST_BOTTOM) &&
            state.getValue(SOUTH_EAST_BOTTOM) &&
            state.getValue(NORTH_EAST_TOP) &&
            state.getValue(SOUTH_EAST_TOP);
    }

}
