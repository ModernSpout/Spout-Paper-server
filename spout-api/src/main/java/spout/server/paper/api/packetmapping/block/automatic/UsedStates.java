package spout.server.paper.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;

/**
 * The states that are chosen as mapping targets for a
 * requested proxy mapping.
 *
 * <p>
 *     Some of the states may be the fallback state instead of the desired proxy state,
 *     in which case the respective {@code isFallback} method will return true.
 * </p>
 */
public interface UsedStates {

    /**
     * @return The used block state,
     * with the same properties as the given {@code reference}.
     */
    org.bukkit.block.data.BlockData get(BlockData reference);

    boolean isFallback(BlockData reference);

    abstract class Wrapper implements UsedStates {

        private final UsedStates internal;

        protected Wrapper(UsedStates internal) {
            this.internal = internal;
        }

        @Override
        public BlockData get(BlockData reference) {
            return this.internal.get(reference);
        }

        @Override
        public boolean isFallback(BlockData reference) {
            return this.internal.isFallback(reference);
        }

    }

    class Powerable extends Wrapper {

        public Powerable(UsedStates internal) {
            super(internal);
        }

        private BlockData createReference(boolean powered) {
            return BlockType.STONE_BUTTON.createBlockData(data -> data.setPowered(powered));
        }

        public org.bukkit.block.data.Powerable get(boolean powered) {
            return (org.bukkit.block.data.Powerable) this.get(this.createReference(powered));
        }

        public boolean isFallback(boolean powered) {
            return this.isFallback(this.createReference(powered));
        }

    }

    class Single extends Wrapper {

        public Single(UsedStates internal) {
            super(internal);
        }

        private BlockData createReference() {
            return BlockType.STONE.createBlockData();
        }

        public org.bukkit.block.data.BlockData get() {
            return this.get(this.createReference());
        }

        public boolean isFallback() {
            return this.isFallback(this.createReference());
        }

    }

    class Slab extends Wrapper {

        public Slab(UsedStates internal) {
            super(internal);
        }

        private BlockData createReference(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged) {
            return BlockType.STONE_SLAB.createBlockData(data -> {
                data.setType(type);
                data.setWaterlogged(waterlogged);
            });
        }

        public org.bukkit.block.data.type.Slab get(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged) {
            return (org.bukkit.block.data.type.Slab) this.get(this.createReference(type, waterlogged));
        }

        public boolean isFallback(org.bukkit.block.data.type.Slab.Type type, boolean waterlogged) {
            return this.isFallback(this.createReference(type, waterlogged));
        }

    }

    class Stairs extends Wrapper {

        public Stairs(UsedStates internal) {
            super(internal);
        }

        private BlockData createReference(org.bukkit.block.BlockFace facing, org.bukkit.block.data.Bisected.Half half, org.bukkit.block.data.type.Stairs.Shape shape, boolean waterlogged) {
            return BlockType.STONE_STAIRS.createBlockData(data -> {
                data.setFacing(facing);
                data.setHalf(half);
                data.setShape(shape);
                data.setWaterlogged(waterlogged);
            });
        }

        public org.bukkit.block.data.type.Stairs get(org.bukkit.block.BlockFace facing, org.bukkit.block.data.Bisected.Half half, org.bukkit.block.data.type.Stairs.Shape shape, boolean waterlogged) {
            return (org.bukkit.block.data.type.Stairs) this.get(this.createReference(facing, half, shape, waterlogged));
        }

        public boolean isFallback(org.bukkit.block.BlockFace facing, org.bukkit.block.data.Bisected.Half half, org.bukkit.block.data.type.Stairs.Shape shape, boolean waterlogged) {
            return this.isFallback(this.createReference(facing, half, shape, waterlogged));
        }

    }

    class Switch extends Wrapper {

        public Switch(UsedStates internal) {
            super(internal);
        }

        private BlockData createReference(org.bukkit.block.data.FaceAttachable.AttachedFace face, org.bukkit.block.BlockFace facing, boolean powered) {
            return BlockType.STONE_BUTTON.createBlockData(data -> {
                data.setAttachedFace(face);
                data.setFacing(facing);
                data.setPowered(powered);
            });
        }

        public org.bukkit.block.data.type.Switch get(org.bukkit.block.data.FaceAttachable.AttachedFace face, org.bukkit.block.BlockFace facing, boolean powered) {
            return (org.bukkit.block.data.type.Switch) this.get(this.createReference(face, facing, powered));
        }

        public boolean isFallback(org.bukkit.block.data.FaceAttachable.AttachedFace face, org.bukkit.block.BlockFace facing, boolean powered) {
            return this.isFallback(this.createReference(face, facing, powered));
        }

    }

    class Waterlogged extends Wrapper {

        public Waterlogged(UsedStates internal) {
            super(internal);
        }

        private BlockData createReference(boolean waterlogged) {
            return BlockType.BARRIER.createBlockData(data -> data.setWaterlogged(waterlogged));
        }

        public org.bukkit.block.data.Waterlogged get(boolean waterlogged) {
            return (org.bukkit.block.data.Waterlogged) this.get(this.createReference(waterlogged));
        }

        public boolean isFallback(boolean waterlogged) {
            return this.isFallback(this.createReference(waterlogged));
        }

    }

}
