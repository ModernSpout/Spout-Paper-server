package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The implementations of wrapped {@link UsedStates} instances.
 *
 * <p>
 * The implementations are generally geared towards low memory consumption and deferred computation.
 * </p>
 */
public final class UsedStatesInternalImpls {

    private UsedStatesInternalImpls() {
        throw new UnsupportedOperationException();
    }

    public static abstract class AbstractBooleanProperty<B extends CraftBlockData> implements UsedStates {

        protected abstract BooleanProperty getProperty();

        protected abstract BlockState getFalseState();

        protected abstract BlockState getTrueState();

        protected abstract boolean getFalseIsFallback();

        protected abstract boolean getTrueIsFallback();

        private boolean getFlag(BlockData reference) {
            return ((CraftBlockData) reference).getState().getValue(this.getProperty());
        }

        @Override
        public B get(BlockData reference) {
            return (B) (this.getFlag(reference) ? this.getTrueState() : this.getFalseState()).createCraftBlockData();
        }

        @Override
        public boolean isFallback(BlockData reference) {
            return this.getFlag(reference) ? this.getTrueIsFallback() : this.getFalseIsFallback();
        }

    }

    public static abstract class AbstractDirectBooleanProperty<B extends CraftBlockData> extends AbstractBooleanProperty<B> {

        private final BlockState falseState;
        private final BlockState trueState;
        private final boolean falseIsFallback;
        private final boolean trueIsFallback;

        public AbstractDirectBooleanProperty(BlockState falseState, BlockState trueState, boolean falseIsFallback, boolean trueIsFallback) {
            this.falseState = falseState;
            this.trueState = trueState;
            this.falseIsFallback = falseIsFallback;
            this.trueIsFallback = trueIsFallback;
        }

        @Override
        protected BlockState getFalseState() {
            return this.falseState;
        }

        @Override
        protected BlockState getTrueState() {
            return this.trueState;
        }

        @Override
        protected boolean getFalseIsFallback() {
            return this.falseIsFallback;
        }

        @Override
        protected boolean getTrueIsFallback() {
            return this.trueIsFallback;
        }

    }

    public static abstract class AbstractBlockBooleanProperty<B extends CraftBlockData> extends AbstractBooleanProperty<B> {

        private final Block block;
        private final boolean falseIsFallback;
        private final boolean trueIsFallback;

        public AbstractBlockBooleanProperty(Block block, boolean falseIsFallback, boolean trueIsFallback) {
            this.block = block;
            this.falseIsFallback = falseIsFallback;
            this.trueIsFallback = trueIsFallback;
        }

        public AbstractBlockBooleanProperty(Block block, boolean isFallback) {
            this(block, isFallback, isFallback);
        }

        @Override
        protected BlockState getFalseState() {
            return this.block.defaultBlockState().setValue(this.getProperty(), false);
        }

        @Override
        protected BlockState getTrueState() {
            return this.block.defaultBlockState().setValue(this.getProperty(), true);
        }

        @Override
        protected boolean getFalseIsFallback() {
            return this.falseIsFallback;
        }

        @Override
        protected boolean getTrueIsFallback() {
            return this.trueIsFallback;
        }

    }

    public static abstract class AbstractMultiProperty<B extends CraftBlockData> implements UsedStates {

        protected abstract List<Property<?>> getProperties();

        protected abstract BlockState[] getStates();

        protected abstract boolean[] getIsFallback();

        protected int getIndex(List<? extends Comparable<?>> propertyValues) {
            List<Property<?>> properties = this.getProperties();
            int size = properties.size();
            int factor = 1;
            int index = 0;
            for (int propertyIndex = 0; propertyIndex < size; propertyIndex++) {
                List<?> possibleValues = properties.get(propertyIndex).getPossibleValues();
                int indexInValues = possibleValues.indexOf(propertyValues.get(propertyIndex));
                index += indexInValues * factor;
                factor *= possibleValues.size();
            }
            return index;
        }

        protected B get(List<? extends Comparable<?>> propertyValues) {
            return (B) this.getStates()[this.getIndex(propertyValues)].createCraftBlockData();
        }

        protected boolean isFallback(List<? extends Comparable<?>> propertyValues) {
            return this.getIsFallback()[this.getIndex(propertyValues)];
        }

        private List<? extends Comparable<?>> getPropertyValues(BlockData reference) {
            BlockState referenceNMS = ((CraftBlockData) reference).getState();
            return this.getProperties().stream().map(referenceNMS::getValue).toList();
        }

        @Override
        public BlockData get(BlockData reference) {
            return this.get(this.getPropertyValues(reference));
        }

        @Override
        public boolean isFallback(BlockData reference) {
            return this.isFallback(this.getPropertyValues(reference));
        }

    }

    public static abstract class AbstractDirectMultiProperty<B extends CraftBlockData> extends AbstractMultiProperty<B> {

        private boolean isUnsorted = true;
        private final BlockState[] states;
        private final boolean[] isFallback;

        public AbstractDirectMultiProperty(BlockState[] states, boolean[] isFallback) {
            this.states = states;
            this.isFallback = isFallback;
        }

        private void sortIfNecessary() {
            if (this.isUnsorted) {
                List<Property<?>> properties = this.getProperties();
                BlockState[] newStates = new BlockState[this.states.length];
                for (int i = 0; i < newStates.length; i++) {
                    BlockState state = this.states[i];
                    newStates[this.getIndex(properties.stream().map(state::getValue).toList())] = state;
                }
                System.arraycopy(newStates, 0, this.states, 0, newStates.length);
                this.isUnsorted = false;
            }
        }

        @Override
        protected BlockState[] getStates() {
            this.sortIfNecessary();
            return this.states;
        }

        @Override
        protected boolean[] getIsFallback() {
            this.sortIfNecessary();
            return this.isFallback;
        }

    }

    public static abstract class AbstractBlockMultiProperty<B extends CraftBlockData> extends AbstractMultiProperty<B> {

        private final Block block;
        private BlockState @Nullable [] states;
        private final boolean isFallback;

        public AbstractBlockMultiProperty(Block block, boolean isFallback) {
            this.block = block;
            this.isFallback = isFallback;
        }

        @Override
        protected BlockState[] getStates() {
            if (this.states == null) {
                List<Property<?>> properties = this.getProperties();
                int size = 1;
                for (Property<?> property : properties) {
                    size *= property.getPossibleValues().size();
                }
                this.states = new BlockState[size];
                int stateIndex = 0;
                int propertyIndex = 0;
                int[] possibleValueIndices = new int[properties.size()];
                Arrays.fill(possibleValueIndices, -1);
                BlockState state = this.block.defaultBlockState();
                while (true) {
                    if (propertyIndex == possibleValueIndices.length) {
                        this.states[stateIndex++] = state;
                        propertyIndex--;
                    } else {
                        int newPossibleValueIndex = ++possibleValueIndices[propertyIndex];
                        if (newPossibleValueIndex == properties.get(propertyIndex).getPossibleValues().size()) {
                            if (propertyIndex == 0) {
                                break;
                            }
                            possibleValueIndices[propertyIndex] = -1;
                            propertyIndex--;
                        } else {
                            state = state.setValue((Property) properties.get(propertyIndex), (Comparable) properties.get(propertyIndex).getPossibleValues().get(newPossibleValueIndex));
                            propertyIndex++;
                        }
                    }
                }
            }
            return this.states;
        }

        @Override
        protected boolean[] getIsFallback() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isFallback(BlockData reference) {
            return this.isFallback;
        }
    }

    public static class BlockDoor<B extends CraftBlockData & org.bukkit.block.data.type.Door> extends AbstractBlockMultiProperty<B> {

        private static final List<Property<?>> PROPERTIES = new ArrayList<>(Blocks.OAK_DOOR.getStateDefinition().getProperties());

        public BlockDoor(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected List<Property<?>> getProperties() {
            return PROPERTIES;
        }

    }

    public static class BlockGate<B extends CraftBlockData & org.bukkit.block.data.type.Gate> extends AbstractBlockMultiProperty<B> {

        private static final List<Property<?>> PROPERTIES = new ArrayList<>(Blocks.OAK_FENCE_GATE.getStateDefinition().getProperties());

        public BlockGate(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected List<Property<?>> getProperties() {
            return PROPERTIES;
        }

    }

    public static class BlockLadder<B extends CraftBlockData & org.bukkit.block.data.type.Ladder> extends AbstractBlockMultiProperty<B> {

        private static final List<Property<?>> PROPERTIES = new ArrayList<>(Blocks.LADDER.getStateDefinition().getProperties());

        public BlockLadder(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected List<Property<?>> getProperties() {
            return PROPERTIES;
        }

    }

    public static class BlockPowerable<B extends CraftBlockData & org.bukkit.block.data.Powerable> extends AbstractBlockBooleanProperty<B> {

        public BlockPowerable(Block block, boolean falseIsFallback, boolean trueIsFallback) {
            super(block, falseIsFallback, trueIsFallback);
        }

        public BlockPowerable(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected BooleanProperty getProperty() {
            return BlockStateProperties.POWERED;
        }

    }

    public static class Single implements UsedStates {

        private final BlockState state;
        private final boolean isFallback;

        public Single(BlockState state, boolean isFallback) {
            this.state = state;
            this.isFallback = isFallback;
        }

        @Override
        public BlockData get(BlockData reference) {
            return this.state.createCraftBlockData();
        }

        @Override
        public boolean isFallback(BlockData reference) {
            return this.isFallback;
        }
    }

    public static class Slab<B extends CraftBlockData & org.bukkit.block.data.type.Slab> implements UsedStates {

        private final BlockState[] nonWaterlogged = new BlockState[org.bukkit.block.data.type.Slab.Type.values().length];
        private final BlockState[] waterlogged = new BlockState[this.nonWaterlogged.length];
        private final boolean[] nonWaterloggedIsFallback = new boolean[this.nonWaterlogged.length];
        private final boolean[] waterloggedIsFallback = new boolean[this.nonWaterlogged.length];

        /**
         * @param data The used block states in the order of the {@link StateDefinition} of
         *             {@link Blocks#STONE_SLAB}.
         */
        public Slab(List<BlockState> data, boolean[] isWaterlogged) {
            List<BlockState> possibleStates = Blocks.STONE_SLAB.getStateDefinition().getPossibleStates();
            for (int i = 0; i < data.size(); i++) {
                BlockState state = possibleStates.get(i);
                int index = CraftBlockData.toBukkit(state.getValue(BlockStateProperties.SLAB_TYPE), org.bukkit.block.data.type.Slab.Type.class).ordinal();
                boolean stateWaterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
                (stateWaterlogged ? this.waterlogged : this.nonWaterlogged)[index] = data.get(i);
                (stateWaterlogged ? this.waterloggedIsFallback : this.nonWaterloggedIsFallback)[index] = isWaterlogged[i];
            }
        }

        public Slab(Block block, boolean isFallback) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                (state.getValue(BlockStateProperties.WATERLOGGED) ? this.waterlogged : this.nonWaterlogged)[CraftBlockData.toBukkit(state.getValue(BlockStateProperties.SLAB_TYPE), org.bukkit.block.data.type.Slab.Type.class).ordinal()] = state;
            }
            Arrays.fill(this.nonWaterloggedIsFallback, isFallback);
            Arrays.fill(this.waterloggedIsFallback, isFallback);
        }

        public B get(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged) {
            return (B) (waterlogged ? this.waterlogged : this.nonWaterlogged)[type.ordinal()].createCraftBlockData();
        }

        public boolean isFallback(final org.bukkit.block.data.type.Slab.Type type, final boolean waterlogged) {
            return (waterlogged ? this.waterloggedIsFallback : this.nonWaterloggedIsFallback)[type.ordinal()];
        }

        @Override
        public BlockData get(BlockData reference) {
            return this.get((reference instanceof org.bukkit.block.data.type.Slab slab ? slab : BlockType.STONE_SLAB.createBlockData()).getType(), (reference instanceof org.bukkit.block.data.Waterlogged waterlogged ? waterlogged : BlockType.STONE_SLAB.createBlockData()).isWaterlogged());
        }

        @Override
        public boolean isFallback(BlockData reference) {
            return this.isFallback((reference instanceof org.bukkit.block.data.type.Slab slab ? slab : BlockType.STONE_SLAB.createBlockData()).getType(), (reference instanceof org.bukkit.block.data.Waterlogged waterlogged ? waterlogged : BlockType.STONE_SLAB.createBlockData()).isWaterlogged());
        }

    }

    public static class BlockStairs<B extends CraftBlockData & org.bukkit.block.data.type.Stairs> extends AbstractBlockMultiProperty<B> {

        private static final List<Property<?>> PROPERTIES = new ArrayList<>(Blocks.OAK_STAIRS.getStateDefinition().getProperties());

        public BlockStairs(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected List<Property<?>> getProperties() {
            return PROPERTIES;
        }

    }

    public static class BlockSwitch<B extends CraftBlockData & org.bukkit.block.data.type.Switch> extends AbstractBlockMultiProperty<B> {

        private static final List<Property<?>> PROPERTIES = new ArrayList<>(Blocks.OAK_BUTTON.getStateDefinition().getProperties());

        public BlockSwitch(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected List<Property<?>> getProperties() {
            return PROPERTIES;
        }

    }

    public static class BlockTrapDoor<B extends CraftBlockData & org.bukkit.block.data.type.TrapDoor> extends AbstractBlockMultiProperty<B> {

        private static final List<Property<?>> PROPERTIES = new ArrayList<>(Blocks.OAK_TRAPDOOR.getStateDefinition().getProperties());

        public BlockTrapDoor(Block block, boolean isFallback) {
            super(block, isFallback);
        }

        @Override
        protected List<Property<?>> getProperties() {
            return PROPERTIES;
        }

    }

    public static class DirectWaterlogged<B extends CraftBlockData & org.bukkit.block.data.Waterlogged> extends AbstractDirectBooleanProperty<B> {

        public DirectWaterlogged(
            BlockState falseState,
            BlockState trueState,
            boolean falseIsFallback,
            boolean trueIsFallback
        ) {
            super(falseState, trueState, falseIsFallback, trueIsFallback);
        }

        @Override
        protected BooleanProperty getProperty() {
            return BlockStateProperties.WATERLOGGED;
        }

    }

}
