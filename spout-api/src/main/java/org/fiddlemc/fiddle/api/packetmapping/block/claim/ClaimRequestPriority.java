package org.fiddlemc.fiddle.api.packetmapping.block.claim;

import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import java.util.Arrays;
import java.util.Comparator;
import java.util .HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A priority for requests made to {@link ResourcePackBlockStateClaims}.
 */
public sealed abstract class ClaimRequestPriority permits ClaimRequestPriority.Explicit, ClaimRequestPriority.ForBlockStates {

    protected ClaimRequestPriority() {
    }

    static final class Explicit extends ClaimRequestPriority {

        final Level level;

        private Explicit(Level level) {
            this.level = level;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof Explicit other && this.level == other.level;
        }

        @Override
        public int hashCode() {
            return this.level.hashCode();
        }

        @Override
        public String toString() {
            return this.level.name().toLowerCase(Locale.ROOT);
        }

        enum Level {
            LOWEST,
            NORMAL,
            HIGHEST;
        }

        private static Map<Level, Explicit> BY_LEVEL = new HashMap<>();

        static Explicit getByLevel(Level level) {
            return BY_LEVEL.computeIfAbsent(level, Explicit::new);
        }

    }

    static final class ForBlockStates extends ClaimRequestPriority {

        private static int nextId = 0; // For first-come first-serve arbitration

        final int id;
        final BlockData[] blockStates;
        final double factor;

        ForBlockStates(BlockData[] blockStates, double factor) {
            this.id = nextId++;
            this.blockStates = blockStates;
            Arrays.sort(this.blockStates, Comparator.comparing(BlockData::getAsString));
            this.factor = factor;
        }

        ForBlockStates(BlockData[] blockStates) {
            this(blockStates, 1);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof ForBlockStates other && this.id == other.id && Arrays.equals(this.blockStates, other.blockStates);
        }

        @Override
        public int hashCode() {
            return 3 + this.id + 138577 * Arrays.hashCode(this.blockStates);
        }

        @Override
        public String toString() {
            return "for block states " + Arrays.toString(this.blockStates);
        }

    }

    /**
     * @return The lowest {@link ClaimRequestPriority}.
     * Claims with this priority will be evaluated after all other claims.
     */
    public static ClaimRequestPriority lowest() {
        return Explicit.getByLevel(Explicit.Level.LOWEST);
    }

    /**
     * @return The default {@link ClaimRequestPriority}.
     *
     * <p>
     * If possible, it is better to use {@link #forBlockState}
     * so that the server can evaluate the claim appropriately.
     * </p>
     */
    public static ClaimRequestPriority normal() {
        return Explicit.getByLevel(Explicit.Level.NORMAL);
    }

    /**
     * @return The lowest {@link ClaimRequestPriority}.
     * Claims with this priority will be evaluated before any other claims.
     *
     * <p>
     * Note that you should only use this {@link ClaimRequestPriority}
     * if you absolutely know what you are doing.
     * Generally, it is best to let the server decide how important your claim is
     * using {@link #forBlockState}.
     * </p>
     */
    public static ClaimRequestPriority highest() {
        return Explicit.getByLevel(Explicit.Level.HIGHEST);
    }

    /**
     * @param blockStates Custom {@link BlockData}s
     *                   (in other words, {@link BlockData}s of a {@link BlockType}
     *                   added by a plugin).
     * @return A {@link ClaimRequestPriority} for the purpose of visualizing the given
     * custom block states.
     *
     * <p>
     * {@link #forBlockState} priorities are always between {@link #normal} and {@link #highest}.
     * </p>
     */
    public static ClaimRequestPriority forBlockStates(BlockData[] blockStates) {
        return new ForBlockStates(blockStates);
    }

    /**
     * The same as {@link #forBlockStates(BlockData[])},
     * but with a custom factor applied afterward.
     *
     * <p>
     * For example, if the server gives your claim a value of 5 to compare it to other claims
     * (which also get assigned a value), and you have given a {@code factor} of 2 to this method,
     * your claim will instead be treated as having a value of 10 (so a higher priority).
     * </p>
     *
     * <p>
     * The server will do its best to assign accurate values to claims.
     * Use this method only when you know that the factor you add accurately represents that
     * this block state has a higher/lower priority than other block states by default.
     * </p>
     */
    public static ClaimRequestPriority forBlockStates(BlockData[] blockStates, double factor) {
        return new ForBlockStates(blockStates, factor);
    }

    /**
     * @see #forBlockStates(BlockData[])
     */
    public static ClaimRequestPriority forBlockState(BlockData blockState) {
        return forBlockStates(new BlockData[]{blockState});
    }

    /**
     * @see #forBlockStates(BlockData[], double)
     */
    public static ClaimRequestPriority forBlockState(BlockData blockState, double factor) {
        return forBlockStates(new BlockData[]{blockState}, factor);
    }

}
