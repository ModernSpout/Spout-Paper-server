package org.fiddlemc.fiddle.impl.packetmapping.block.breakspeed;

import net.minecraft.server.level.ServerPlayer;

/**
 * Stores the history of {@link ServerPlayer#serverToClientSideBlockBreakSpeedFactor}
 * for a player.
 */
public final class BlockBreakSpeedFactorHistory {

    private float[] values = new float[20];

    /**
     * The start index of the history in {@link #values}, inclusive.
     * This is a value in the range 0 (inclusive) to {@link #values}{@code .length - 1} (inclusive).
     */
    private int startIndex;

    /**
     * The end index of the history in {@link #values}, exclusive.
     * This is a value in the range 0 (inclusive) to {@link #values}{@code .length - 1} (inclusive).
     *
     * <p>
     * If it is equal to {@link #startIndex}, there are no values in this history.
     * </p>
     */
    private int endIndex;

    public void storeNewValue(ServerPlayer player, float value) {
        int newLength = getIntendedHistoryLength(player);
        int currentLength = this.startIndex <= this.endIndex ? this.endIndex - this.startIndex : this.values.length - startIndex + this.endIndex;
        // Shorten the history if necessary
        int toShorten = currentLength - (newLength - 1);
        if (toShorten > 0) {
            if ((this.startIndex += toShorten) >= this.values.length) {
                this.startIndex -= this.values.length;
            }
        }
        // Add the new value
        int newValueIndex = this.endIndex;
        if (++this.endIndex == this.values.length) {
            this.endIndex = 0;
        }
        if (this.endIndex == this.startIndex) {
            // Grow the array
            float[] newValues = new float[values.length * 2];
            System.arraycopy(this.values, this.startIndex, newValues, 0, this.values.length - this.startIndex);
            if (this.startIndex != 0) {
                System.arraycopy(this.values, 0, newValues, this.values.length - this.startIndex, this.startIndex);
            }
            this.startIndex = 0;
            this.endIndex = this.values.length;
            this.values = newValues;
            newValueIndex = this.endIndex - 1;
        }
        this.values[newValueIndex] = value;
    }

    public float getHighestValue() {
        float highestValue = 1; // Always allow at least 1
        int index = this.startIndex;
        while (index != this.endIndex) {
            float value = this.values[index];
            if (value > highestValue) {
                highestValue = value;
            }
            if (++index == this.values.length) {
                index = 0;
            }
        }
        return highestValue;
    }

    /**
     * @return The intended history length for the player, in ticks.
     */
    private static int getIntendedHistoryLength(ServerPlayer player) {
        int length = player.connection.latency() * 3 / 50;
        if (player.gameMode.isDestroyingBlock) {
            int destroyTime = player.gameMode.gameTicks - player.gameMode.destroyProgressStart;
            if (destroyTime > length) {
                length = destroyTime;
            }
        }
        return Math.min(length, 399) + 1;
    }

}
