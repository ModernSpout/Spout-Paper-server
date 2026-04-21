package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import java.util.Arrays;
import java.util.List;

/**
 * The implementation of {@link UsedStates}.
 */
public final class UsedStatesImpl {

    public static final class SingleImpl implements UsedStates.Single {

        private final BlockState state;
        private final boolean isFallback;

        public SingleImpl(BlockState state, boolean isFallback) {
            this.state = state;
            this.isFallback = isFallback;
        }

        @Override
        public BlockData get() {
            return this.state.createCraftBlockData();
        }

        @Override
        public boolean isFallback() {
            return this.isFallback;
        }
    }

    public static final class WaterloggedImpl implements UsedStates.Waterlogged {

        private final BlockState nonWaterlogged;
        private final BlockState waterlogged;
        private final boolean nonWaterloggedIsFallback;
        private final boolean waterloggedIsFallback;

        public WaterloggedImpl(
            BlockState nonWaterlogged,
            BlockState waterlogged,
            boolean nonWaterloggedIsFallback,
            boolean waterloggedIsFallback
        ) {
            this.nonWaterlogged = nonWaterlogged;
            this.waterlogged = waterlogged;
            this.nonWaterloggedIsFallback = nonWaterloggedIsFallback;
            this.waterloggedIsFallback = waterloggedIsFallback;
        }

        @Override
        public BlockData get(boolean waterlogged) {
            return (waterlogged ? this.waterlogged : this.nonWaterlogged).createCraftBlockData();
        }

        @Override
        public boolean isFallback(final boolean waterlogged) {
            return waterlogged ?  this.waterloggedIsFallback : this.nonWaterloggedIsFallback;
        }
    }

    public static final class SlabImpl implements UsedStates.Slab {

        private final BlockState[] nonWaterlogged = new BlockState[org.bukkit.block.data.type.Slab.Type.values().length];
        private final BlockState[] waterlogged = new BlockState[this.nonWaterlogged.length];
        private final boolean[] nonWaterloggedIsFallback = new boolean[this.nonWaterlogged.length];
        private final boolean[] waterloggedIsFallback = new boolean[this.nonWaterlogged.length];

        /**
         * @param data The used block states in the order of the {@link StateDefinition} of
         * {@link Blocks#STONE_SLAB}.
         */
        public SlabImpl(List<BlockState> data, boolean[] isWaterlogged) {
            List<BlockState> possibleStates = Blocks.STONE_SLAB.getStateDefinition().getPossibleStates();
            for (int i = 0; i < data.size(); i++) {
                BlockState state = possibleStates.get(i);
                int index = CraftBlockData.toBukkit(state.getValue(BlockStateProperties.SLAB_TYPE), org.bukkit.block.data.type.Slab.Type.class).ordinal();
                boolean stateWaterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
                (stateWaterlogged ? this.waterlogged : this.nonWaterlogged)[index] = data.get(i);
                (stateWaterlogged ? this.waterloggedIsFallback : this.nonWaterloggedIsFallback)[index] = isWaterlogged[i];
            }
        }

        public SlabImpl(Block block, boolean isFallback) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                (state.getValue(BlockStateProperties.WATERLOGGED) ? this.waterlogged : this.nonWaterlogged)[CraftBlockData.toBukkit(state.getValue(BlockStateProperties.SLAB_TYPE), org.bukkit.block.data.type.Slab.Type.class).ordinal()] = state;
            }
            Arrays.fill(this.nonWaterloggedIsFallback, isFallback);
            Arrays.fill(this.waterloggedIsFallback, isFallback);
        }

        @Override
        public BlockData get(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged) {
            return (waterlogged ? this.waterlogged : this.nonWaterlogged)[type.ordinal()].createCraftBlockData();
        }

        @Override
        public boolean isFallback(final org.bukkit.block.data.type.Slab.Type type, final boolean waterlogged) {
            return (waterlogged ? this.waterloggedIsFallback : this.nonWaterloggedIsFallback)[type.ordinal()];
        }
    }

    public static final class StairsImpl implements UsedStates.Stairs {

        /**
         * The used block states, in the order of the {@link StateDefinition} of
         * {@link Blocks#STONE_STAIRS}.
         */
        private final BlockState[] data;

        /**
         * Whether each state is a fallback, in the order of the {@link StateDefinition} of
         * {@link Blocks#STONE_STAIRS}.
         */
        private final boolean[] isFallback;

        public StairsImpl(BlockState[] data, boolean[] isFallback) {
            this.data = data;
            this.isFallback = isFallback;
        }

        public StairsImpl(Block block, boolean isFallback) {
            List<BlockState> possibleStates = Blocks.STONE_STAIRS.getStateDefinition().getPossibleStates();
            this.data = new BlockState[possibleStates.size()];
            for (int i = 0; i < possibleStates.size(); i++) {
                BlockState state = possibleStates.get(i);
                BlockState stateForBlock = block.defaultBlockState();
                for (Property<?> property : state.getProperties()) {
                    stateForBlock = stateForBlock.setValue((Property) property, state.getValue(property));
                }
                this.data[i] = stateForBlock;
            }
            this.isFallback = new boolean[this.data.length];
            Arrays.fill(this.isFallback, isFallback);
        }

        private int getIndex(org.bukkit.block.data.type.Stairs.Shape shape, Bisected.Half half, BlockFace facing, boolean waterlogged) {
            List<BlockState> possibleStates = Blocks.STONE_STAIRS.getStateDefinition().getPossibleStates();
            for (int i = 0; i < this.data.length; i++) {
                BlockState state = possibleStates.get(i);
                if (CraftBlockData.toBukkit(state.getValue(BlockStateProperties.STAIRS_SHAPE), org.bukkit.block.data.type.Stairs.Shape.class) == shape) {
                    if (CraftBlockData.toBukkit(state.getValue(BlockStateProperties.HALF), Bisected.Half.class) == half) {
                        if (state.getValue(BlockStateProperties.WATERLOGGED) == waterlogged) {
                            return i;
                        }
                    }
                }
            }
            throw new IllegalStateException();
        }

        @Override
        public BlockData get(org.bukkit.block.data.type.Stairs.Shape shape, Bisected.Half half, BlockFace facing, boolean waterlogged) {
            return this.data[this.getIndex(shape, half, facing, waterlogged)].createCraftBlockData();
        }

        @Override
        public boolean isFallback(org.bukkit.block.data.type.Stairs.Shape shape, Bisected.Half half, BlockFace facing, boolean waterlogged) {
            return this.isFallback[this.getIndex(shape, half, facing, waterlogged)];
        }

    }

}
