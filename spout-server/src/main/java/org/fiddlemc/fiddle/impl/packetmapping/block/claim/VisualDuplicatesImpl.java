package org.fiddlemc.fiddle.impl.packetmapping.block.claim;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.VisualDuplicates;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import org.fiddlemc.fiddle.impl.util.datastructure.UnionFind;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The implementation of {@link VisualDuplicates}.
 */
public final class VisualDuplicatesImpl implements VisualDuplicates {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<VisualDuplicates, VisualDuplicatesImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(VisualDuplicatesImpl.class);
        }

    }

    public static VisualDuplicatesImpl get() {
        return (VisualDuplicatesImpl) VisualDuplicates.get();
    }

    @Override
    public @Nullable VisualDuplicateGroup getVisualDuplicates(BlockData blockState) {
        return this.getVisualDuplicates(((CraftBlockData) blockState).getState());
    }

    @Override
    public boolean hasVisualDuplicates(BlockData blockState) {
        return this.hasVisualDuplicates(((CraftBlockData) blockState).getState());
    }

    public @Nullable VisualDuplicateGroupImpl getVisualDuplicates(BlockState blockState) {
        return this.getVisualDuplicates(blockState.indexInVanillaOnlyBlockStateRegistry);
    }

    public boolean hasVisualDuplicates(BlockState blockState) {
        return this.hasVisualDuplicates(blockState.indexInVanillaOnlyBlockStateRegistry);
    }

    public @Nullable VisualDuplicateGroupImpl getVisualDuplicates(int blockStateId) {
        initializeIfNecessary();
        return map[blockStateId];
    }

    public boolean hasVisualDuplicates(int blockStateId) {
        return this.getVisualDuplicates(blockStateId) != null;
    }

    /**
     * The internal map used to lookup duplicates,
     * or null if not {@linkplain #initializeIfNecessary() initialized} yet.
     *
     * <p>
     * Keys in the map are the {@link BlockState#indexInVanillaOnlyBlockStateRegistry}
     * of block states. Not every key maps to a non-null value. If null, it means there are no visual duplicates
     * for that {@link BlockState}.
     * </p>
     */
    private static @Nullable VisualDuplicateGroupImpl @Nullable [] map;

    private static void initializeIfNecessary() {
        if (map != null) return;
        // Create a union find structure
        int size = VanillaOnlyBlockStateRegistry.get().size();
        if (size == 0) {
            throw new IllegalStateException("Called VisualDuplicates while block state registry has not been initialized yet");
        }
        BlockStateUnionFind unionFind = new BlockStateUnionFind(size);
        // Add all the duplicates
        addDuplicates(unionFind);
        // Build the map
        int[] counts = new int[size];
        for (int i = 0; i < size; i++) {
            counts[unionFind.find(i)]++;
        }
        map = new VisualDuplicateGroupImpl[size];
        for (int i = 0; i < size; i++) {
            if (counts[i] > 1) {
                map[i] = new VisualDuplicateGroupImpl(counts[i]);
            }
        }
        int[] groupEndIndex = new int[size];
        for (int i = 0; i < size; i++) {
            int group = unionFind.find(i);
            if (counts[group] > 1) {
                map[group].stateIndices[groupEndIndex[group]++] = i;
                map[i] = map[group];
            }
        }
        for (int i = 0; i < size; i++) {
            if (map[i] != null) {
                map[i].sort();
            }
        }
    }

    /**
     * Extension of {@link UnionFind} with useful methods for merging {@link BlockState}s.
     */
    private static class BlockStateUnionFind extends UnionFind {

        public BlockStateUnionFind(int size) {
            super(size);
        }

        public void merge(BlockState state1, BlockState state2) {
            this.merge(state1.indexInVanillaOnlyBlockStateRegistry, state2.indexInVanillaOnlyBlockStateRegistry);
        }

        public void mergeDefaults(Block block1, Block block2) {
            this.merge(block1.defaultBlockState(), block2.defaultBlockState());
        }

        public void mergeStateByState(Block block1, Block block2) {
            for (BlockState state1 : block1.getStateDefinition().getPossibleStates()) {
                BlockState state2 = block2.defaultBlockState();
                for (Property<?> property : block1.getStateDefinition().getProperties()) {
                    state2 = state2.setValue((Property) property, state1.getValue(property));
                }
                this.merge(state1, state2);
            }
        }

        public void mergeAll(List<BlockState> states) {
            for (int i = 1; i < states.size(); i++) {
                this.merge(states.getFirst(), states.get(i));
            }
        }

        public void mergeAll(BlockState... states) {
            this.mergeAll(Arrays.asList(states));
        }

        public void mergeAllStatesOf(Block block) {
            this.mergeAll(block.getStateDefinition().getPossibleStates());
        }

        public void mergeAllStatesWithOtherValuesOfProperties(List<BlockState> states, Property<?>... properties) {
            for (BlockState state : states) {
                BlockState stateNormalForm = state;
                for (Property<?> property : properties) {
                    stateNormalForm = stateNormalForm.setValue((Property) property, (Comparable) property.getPossibleValues().getFirst());
                }
                this.merge(stateNormalForm, state);
            }
        }

        public void mergeAllStatesWithOtherValuesOfProperties(Block block, Property<?>... properties) {
            this.mergeAllStatesWithOtherValuesOfProperties(block.getStateDefinition().getPossibleStates(), properties);
        }

        public void mergeDoubleSlabStateToFullBlockState(Block slab, BlockState fullBlockState) {
            this.merge(slab.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE), fullBlockState);
        }

        public void mergeDoubleSlabStateToFullBlockDefaultState(Block slab, Block fullBlock) {
            this.mergeDoubleSlabStateToFullBlockState(slab, fullBlock.defaultBlockState());
        }

    }

    private static Stream<Block> getBlocksOfType(Class<? extends Block> type) {
        return StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(type::isInstance);
    }

    private static <T extends Comparable<T>> List<BlockState> getStatesWhere(Block block, Property<T> property, Object value) {
        return block.getStateDefinition().getPossibleStates().stream().filter(state -> state.getValue(property).equals(value)).toList();
    }

    private static <T extends Comparable<T>> List<BlockState> getStatesWhereFulfills(Block block, Property<T> property, Predicate<Object> predicate) {
        return block.getStateDefinition().getPossibleStates().stream().filter(state -> predicate.test(state.getValue(property))).toList();
    }

    private static void addDuplicates(BlockStateUnionFind unionFind) {
        // Air
        unionFind.mergeAll(Blocks.AIR.defaultBlockState(), Blocks.CAVE_AIR.defaultBlockState(), Blocks.VOID_AIR.defaultBlockState());
        // Bamboo
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.BAMBOO, BlockStateProperties.STAGE);
        // Bed
        getBlocksOfType(BedBlock.class).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.OCCUPIED));
        // Beehive and bee nest
        for (Block block : new Block[]{Blocks.BEEHIVE, Blocks.BEE_NEST}) {
            unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhereFulfills(block, BlockStateProperties.LEVEL_HONEY, honeyLevel -> !honeyLevel.equals(5)), BlockStateProperties.LEVEL_HONEY);
        }
        // Bell
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.BELL, BlockStateProperties.POWERED);
        // Button
        getBlocksOfType(ButtonBlock.class).forEach(block -> {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
                if (face == AttachFace.FLOOR || face == AttachFace.CEILING) {
                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    if (facing == Direction.SOUTH) {
                        unionFind.merge(state, state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
                    } else if (facing == Direction.WEST) {
                        unionFind.merge(state, state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
                    }
                }
            }
        });
        // Cave vines, kelp, twisting vines and weeping vines
        for (Block block : new Block[]{Blocks.CAVE_VINES, Blocks.KELP, Blocks.TWISTING_VINES, Blocks.WEEPING_VINES}) {
            unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.AGE_25);
        }
        // Chorus flower
        unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhereFulfills(Blocks.CHORUS_FLOWER, BlockStateProperties.AGE_5, age -> !age.equals(5)), BlockStateProperties.AGE_5);
        // Copper
        unionFind.mergeStateByState(Blocks.WAXED_CHISELED_COPPER, Blocks.CHISELED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_BLOCK, Blocks.COPPER_BLOCK);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_BULB, Blocks.COPPER_BULB);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_CHEST, Blocks.COPPER_CHEST);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_DOOR, Blocks.COPPER_DOOR);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_GOLEM_STATUE, Blocks.COPPER_GOLEM_STATUE);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_GRATE, Blocks.COPPER_GRATE);
        unionFind.mergeStateByState(Blocks.WAXED_COPPER_TRAPDOOR, Blocks.COPPER_TRAPDOOR);
        unionFind.mergeStateByState(Blocks.WAXED_CUT_COPPER, Blocks.CUT_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_CUT_COPPER_SLAB, Blocks.CUT_COPPER_SLAB);
        unionFind.mergeStateByState(Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.CUT_COPPER_STAIRS);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER, Blocks.EXPOSED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.EXPOSED_COPPER_BULB);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER_CHEST, Blocks.EXPOSED_COPPER_CHEST);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE, Blocks.EXPOSED_COPPER_GOLEM_STATUE);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS);
        unionFind.mergeStateByState(Blocks.WAXED_EXPOSED_LIGHTNING_ROD, Blocks.EXPOSED_LIGHTNING_ROD);
        unionFind.mergeStateByState(Blocks.WAXED_LIGHTNING_ROD, Blocks.LIGHTNING_ROD);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER, Blocks.OXIDIZED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER_CHEST, Blocks.OXIDIZED_COPPER_CHEST);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE, Blocks.OXIDIZED_COPPER_GOLEM_STATUE);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS);
        unionFind.mergeStateByState(Blocks.WAXED_OXIDIZED_LIGHTNING_ROD, Blocks.OXIDIZED_LIGHTNING_ROD);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER, Blocks.WEATHERED_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER_CHEST, Blocks.WEATHERED_COPPER_CHEST);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE, Blocks.WEATHERED_COPPER_GOLEM_STATUE);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS);
        unionFind.mergeStateByState(Blocks.WAXED_WEATHERED_LIGHTNING_ROD, Blocks.WEATHERED_LIGHTNING_ROD);
        // Creaking heart
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.CREAKING_HEART, BlockStateProperties.NATURAL);
        // Crop
        for (Block block : new Block[]{Blocks.CARROTS, Blocks.POTATOES}) {
            unionFind.merge(block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 1), block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 0));
            unionFind.merge(block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 3), block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 2));
            unionFind.merge(block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 5), block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 4));
            unionFind.merge(block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 6), block.defaultBlockState().setValue(BlockStateProperties.AGE_7, 4));
        }
        unionFind.merge(Blocks.NETHER_WART.defaultBlockState().setValue(BlockStateProperties.AGE_3, 2), Blocks.NETHER_WART.defaultBlockState().setValue(BlockStateProperties.AGE_3, 1));
        // Daylight detector
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.DAYLIGHT_DETECTOR, BlockStateProperties.POWER);
        // Dispenser and dropper
        for (Block block : new Block[]{Blocks.DISPENSER, Blocks.DROPPER}) {
            unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.TRIGGERED);
        }
        // Door
        getBlocksOfType(DoorBlock.class).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.POWERED));
        // TODO there are some more duplicate door states, due to the way open doors in one direction can be the same as closed doors in another direction
        // Double slab
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.ANDESITE_SLAB, Blocks.ANDESITE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.BAMBOO_MOSAIC_SLAB, Blocks.BAMBOO_MOSAIC);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.BAMBOO_SLAB, Blocks.BAMBOO_BLOCK);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.BIRCH_SLAB, Blocks.BIRCH_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.BRICK_SLAB, Blocks.BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.CHERRY_SLAB, Blocks.CHERRY_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.DIORITE_SLAB, Blocks.DIORITE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.GRANITE_SLAB, Blocks.GRANITE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.JUNGLE_SLAB, Blocks.JUNGLE_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.MANGROVE_SLAB, Blocks.MANGROVE_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.OAK_SLAB, Blocks.OAK_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.PALE_OAK_SLAB, Blocks.PALE_OAK_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.PETRIFIED_OAK_SLAB, Blocks.OAK_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.POLISHED_TUFF_SLAB, Blocks.POLISHED_TUFF);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.QUARTZ_SLAB, Blocks.QUARTZ_BLOCK);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.RESIN_BRICK_SLAB, Blocks.RESIN_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.STONE_SLAB, Blocks.STONE);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.TUFF_BRICK_SLAB, Blocks.TUFF_BRICKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.TUFF_SLAB, Blocks.TUFF);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.WARPED_SLAB, Blocks.WARPED_PLANKS);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.WAXED_CUT_COPPER_SLAB, Blocks.CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER);
        unionFind.mergeDoubleSlabStateToFullBlockDefaultState(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER);
        // End portal frame
        getStatesWhere(Blocks.END_PORTAL_FRAME, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).forEach(state -> unionFind.merge(state, state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)));
        getStatesWhere(Blocks.END_PORTAL_FRAME, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).forEach(state -> unionFind.merge(state, state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)));
        // Every state is the same
        unionFind.mergeAllStatesOf(Blocks.CACTUS);
        unionFind.mergeAllStatesOf(Blocks.JUKEBOX);
        unionFind.mergeAllStatesOf(Blocks.NOTE_BLOCK);
        unionFind.mergeAllStatesOf(Blocks.SUGAR_CANE);
        unionFind.mergeAllStatesOf(Blocks.TARGET);
        unionFind.mergeAllStatesOf(Blocks.TNT);
        // Farmland
        unionFind.mergeAll(getStatesWhereFulfills(Blocks.FARMLAND, BlockStateProperties.MOISTURE, moisture -> !moisture.equals(7)));
        // Fence gate
        getBlocksOfType(FenceGateBlock.class).forEach(block -> {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                BlockState normalFormState = state;
                normalFormState = normalFormState.setValue(BlockStateProperties.POWERED, false);
                if (!state.getValue(BlockStateProperties.OPEN)) {
                    if (state.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
                        normalFormState = normalFormState.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
                    } else if (state.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST) {
                        normalFormState = normalFormState.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
                    }
                }
                unionFind.merge(normalFormState, state);
            }
        });
        // Hopper
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.HOPPER, BlockStateProperties.ENABLED);
        // Growing plant head
        getBlocksOfType(GrowingPlantHeadBlock.class).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.AGE_25));
        // Infested
        unionFind.mergeDefaults(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        unionFind.mergeDefaults(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        unionFind.mergeDefaults(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        unionFind.mergeStateByState(Blocks.INFESTED_DEEPSLATE, Blocks.DEEPSLATE);
        unionFind.mergeDefaults(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        unionFind.mergeDefaults(Blocks.INFESTED_STONE, Blocks.STONE);
        unionFind.mergeDefaults(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        // Leaves
        getBlocksOfType(LeavesBlock.class).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.DISTANCE, BlockStateProperties.PERSISTENT));
        // Lectern
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.LECTERN, BlockStateProperties.POWERED);
        // Mangrove propagule
        unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhere(Blocks.MANGROVE_PROPAGULE, BlockStateProperties.HANGING, true), BlockStateProperties.STAGE);
        unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhere(Blocks.MANGROVE_PROPAGULE, BlockStateProperties.HANGING, false), BlockStateProperties.STAGE, BlockStateProperties.HANGING);
        // Petrified oak slab
        unionFind.mergeStateByState(Blocks.PETRIFIED_OAK_SLAB, Blocks.OAK_SLAB);
        // Sapling
        getBlocksOfType(SaplingBlock.class).filter(block -> !(block instanceof MangrovePropaguleBlock)).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.STAGE));
        // Scaffolding
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.SCAFFOLDING, BlockStateProperties.STABILITY_DISTANCE);
        // Sculk sensor
        for (Block block : new Block[]{Blocks.CALIBRATED_SCULK_SENSOR, Blocks.SCULK_SENSOR}) {
            unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.POWER);
        }
        // Shelf
        getBlocksOfType(ShelfBlock.class).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhere(block, BlockStateProperties.POWERED, false), BlockStateProperties.SIDE_CHAIN_PART));
        // Small dripleaf
        unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhere(Blocks.SMALL_DRIPLEAF, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), BlockStateProperties.HORIZONTAL_FACING);
        // Snowy mycelium and podzol
        for (Block block : new Block[]{Blocks.MYCELIUM, Blocks.PODZOL}) {
            unionFind.merge(block.defaultBlockState().setValue(BlockStateProperties.SNOWY, true), Blocks.GRASS_BLOCK.defaultBlockState().setValue(BlockStateProperties.SNOWY, true));
        }
        // TODO some stair states are duplicates (one direction's left inner state is the same as the other direction's right inner state, and so on)
        // Trapdoor
        getBlocksOfType(TrapDoorBlock.class).forEach(block -> unionFind.mergeAllStatesWithOtherValuesOfProperties(block, BlockStateProperties.POWERED));
        // Tripwire
        unionFind.mergeAllStatesWithOtherValuesOfProperties(Blocks.TRIPWIRE, BlockStateProperties.ATTACHED, BlockStateProperties.POWERED); // TODO Potentially not accurate anymore for 26.1
        // Tripwire hook
        unionFind.mergeAllStatesWithOtherValuesOfProperties(getStatesWhere(Blocks.TRIPWIRE_HOOK, BlockStateProperties.ATTACHED, false), BlockStateProperties.POWERED);
        // Wall
        getBlocksOfType(WallBlock.class).forEach(block -> {
            BlockState airState = block.defaultBlockState()
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.EAST_WALL, WallSide.NONE)
                .setValue(BlockStateProperties.NORTH_WALL, WallSide.NONE)
                .setValue(BlockStateProperties.SOUTH_WALL, WallSide.NONE)
                .setValue(BlockStateProperties.WEST_WALL, WallSide.NONE)
                .setValue(BlockStateProperties.WATERLOGGED, false);
            unionFind.merge(Blocks.AIR.defaultBlockState(), airState);
            unionFind.merge(Blocks.WATER.defaultBlockState(), airState.setValue(BlockStateProperties.WATERLOGGED, true));
        });
        // Weighted pressure plate
        for (Block block : new Block[]{Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE}) {
            unionFind.mergeAll(getStatesWhereFulfills(block, BlockStateProperties.POWER, power -> !power.equals(0)));
        }
    }

    /**
     * The implementation of {@link VisualDuplicateGroup}.
     */
    public static class VisualDuplicateGroupImpl implements VisualDuplicateGroup {

        /**
         * The {@link BlockState#indexInVanillaOnlyBlockStateRegistry}
         * of every block state in this group.
         */
        public final int[] stateIndices;

        /**
         * The cached return value of {@link #getStates()},
         * or null if not initialized yet.
         */
        private @Nullable List<? extends BlockData> states;

        private VisualDuplicateGroupImpl(int size) {
            this.stateIndices = new int[size];
        }

        @Override
        public List<? extends BlockData> getStates() {
            if (this.states == null) {
                this.states = Arrays.stream(this.stateIndices).mapToObj(index -> VanillaOnlyBlockStateRegistry.get().byId(index).createCraftBlockData()).toList();
            }
            return this.states;
        }

        public boolean contains(int id) {
            for (int index : this.stateIndices) {
                if (index == id) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return A unique identifier for this group:
         * which is simply the first element of {@link #stateIndices}.
         */
        public int getId() {
            return this.stateIndices[0];
        }

        /**
         * Sorts {@link #stateIndices} from most to least desirable to be used as a target
         * for a mapping to a visual duplicate. Typically, these are the blocks that behave
         * most like an average block, or are very common.
         */
        private void sort() {
            BlockState[] states = new BlockState[this.stateIndices.length];
            for (int i = 0; i < states.length; i++) {
                states[i] = VanillaOnlyBlockStateRegistry.get().byId(this.stateIndices[i]);
            }
            Arrays.sort(states, STATE_COMPARATOR);
            for (int i = 0; i < states.length; i++) {
                this.stateIndices[i] = states[i].indexInVanillaOnlyBlockStateRegistry;
            }
        }

        private static @Nullable IntSet moreCommonBlocks = null;

        public static IntSet getMoreCommonBlocks() {
            if (moreCommonBlocks == null) {
                moreCommonBlocks = IntSet.of(Set.of(
                    // Air
                    Blocks.AIR,
                    // Copper
                    Blocks.WAXED_CHISELED_COPPER,
                    Blocks.WAXED_COPPER_BLOCK,
                    Blocks.WAXED_COPPER_BULB,
                    Blocks.WAXED_COPPER_CHEST,
                    Blocks.WAXED_COPPER_DOOR,
                    Blocks.WAXED_COPPER_GOLEM_STATUE,
                    Blocks.WAXED_COPPER_GRATE,
                    Blocks.WAXED_COPPER_TRAPDOOR,
                    Blocks.WAXED_CUT_COPPER,
                    Blocks.WAXED_CUT_COPPER_SLAB,
                    Blocks.WAXED_CUT_COPPER_STAIRS,
                    Blocks.WAXED_EXPOSED_CHISELED_COPPER,
                    Blocks.WAXED_EXPOSED_COPPER,
                    Blocks.WAXED_EXPOSED_COPPER_BULB,
                    Blocks.WAXED_EXPOSED_COPPER_CHEST,
                    Blocks.WAXED_EXPOSED_COPPER_DOOR,
                    Blocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE,
                    Blocks.WAXED_EXPOSED_COPPER_GRATE,
                    Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR,
                    Blocks.WAXED_EXPOSED_CUT_COPPER,
                    Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                    Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                    Blocks.WAXED_EXPOSED_LIGHTNING_ROD,
                    Blocks.WAXED_LIGHTNING_ROD,
                    Blocks.WAXED_OXIDIZED_CHISELED_COPPER,
                    Blocks.WAXED_OXIDIZED_COPPER,
                    Blocks.WAXED_OXIDIZED_COPPER_BULB,
                    Blocks.WAXED_OXIDIZED_COPPER_CHEST,
                    Blocks.WAXED_OXIDIZED_COPPER_DOOR,
                    Blocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE,
                    Blocks.WAXED_OXIDIZED_COPPER_GRATE,
                    Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR,
                    Blocks.WAXED_OXIDIZED_CUT_COPPER,
                    Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
                    Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
                    Blocks.WAXED_OXIDIZED_LIGHTNING_ROD,
                    Blocks.WAXED_WEATHERED_CHISELED_COPPER,
                    Blocks.WAXED_WEATHERED_COPPER,
                    Blocks.WAXED_WEATHERED_COPPER_BULB,
                    Blocks.WAXED_WEATHERED_COPPER_CHEST,
                    Blocks.WAXED_WEATHERED_COPPER_DOOR,
                    Blocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE,
                    Blocks.WAXED_WEATHERED_COPPER_GRATE,
                    Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR,
                    Blocks.WAXED_WEATHERED_CUT_COPPER,
                    Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                    Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
                    Blocks.WAXED_WEATHERED_LIGHTNING_ROD,
                    // Infested block
                    Blocks.CHISELED_STONE_BRICKS,
                    Blocks.COBBLESTONE,
                    Blocks.CRACKED_STONE_BRICKS,
                    Blocks.DEEPSLATE,
                    Blocks.MOSSY_STONE_BRICKS,
                    Blocks.STONE,
                    Blocks.STONE_BRICKS,
                    // Petrified oak slab
                    Blocks.OAK_SLAB,
                    // Snowy mycelium and podzol
                    Blocks.GRASS_BLOCK
                ).stream().mapToInt(block -> block.indexInVanillaOnlyBlockRegistry).toArray());
            }
            return moreCommonBlocks;
        }

        private static final Comparator<BlockState> NOTE_BLOCK_STATE_COMPARATOR = Comparator
            // Don't choose the default state
            .<BlockState, Boolean>comparing(state -> state == state.getBlock().defaultBlockState())
            // Prefer unpowered states
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.POWERED, false))
            // Prefer common note block instruments
            .thenComparingInt(state -> {
                NoteBlockInstrument instrument = state.getValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT);
                if (instrument == NoteBlockInstrument.HARP) {
                    return -2;
                } else if (instrument == NoteBlockInstrument.BASS) {
                    return -1;
                }
                return instrument.ordinal();
            })
            // Prefer note block default tone
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.NOTE, 0));

        private static final Comparator<BlockState> NON_NOTE_BLOCK_STATE_COMPARATOR = Comparator
            // Choose regular blocks over blocks with properties
            .<BlockState, Boolean>comparing(state -> !state.getBlock().getStateDefinition().getProperties().isEmpty())
            // Choose anything over a double slab
            .thenComparing(state -> (boolean) state.getOptionalValue(BlockStateProperties.SLAB_TYPE).map(value -> value == SlabType.DOUBLE).orElse(false))
            // Don't choose the default state
            .thenComparing(state -> state == state.getBlock().defaultBlockState())
            // Never prefer blocks that are more common than a visual duplicate of themselves
            .thenComparing(state -> getMoreCommonBlocks().contains(state.getBlock().indexInVanillaOnlyBlockRegistry))
            // Prefer unpowered states
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.POWERED, false))
            // Prefer non-waterlogged states
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.WATERLOGGED, false))
            // Prefer inactive states
            .thenComparingInt(state -> state.getValueOrElse(BlockStateProperties.POWER, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.TRIGGERED, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.CAN_SUMMON, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.ENABLED, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.HAS_BOOK, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.LIT, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.OCCUPIED, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.SHRIEKING, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.SIGNAL_FIRE, false))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.SNOWY, false))
            // Prefer persistent leaves
            .thenComparing(state -> !state.getValueOrElse(BlockStateProperties.PERSISTENT, true))
            // Prefer stable TNT
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.UNSTABLE, false))
            // Prefer low age
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_1, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_2, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_3, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_4, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_5, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_7, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_15, 0))
            .thenComparing(state -> state.getValueOrElse(BlockStateProperties.AGE_25, 0));

        public static final Comparator<BlockState> STATE_COMPARATOR = (state1, state2) -> {
            if (state1.getBlock() == Blocks.NOTE_BLOCK && state2.getBlock() == Blocks.NOTE_BLOCK) {
                // Fast path for performance due to how many duplicate note block states there are
                return NOTE_BLOCK_STATE_COMPARATOR.compare(state1, state2);
            }
            return NON_NOTE_BLOCK_STATE_COMPARATOR.compare(state1, state2);
        };

    }

}
