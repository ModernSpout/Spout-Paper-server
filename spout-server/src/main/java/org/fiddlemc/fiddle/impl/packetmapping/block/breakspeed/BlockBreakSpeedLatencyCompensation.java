package org.fiddlemc.fiddle.impl.packetmapping.block.breakspeed;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Allows players to mine blocks faster than we'd expect due to the block break speed factor that we sent them.
 * If the block break speed factor we should send the player for a certain block is
 */
public final class BlockBreakSpeedLatencyCompensation {

    private BlockBreakSpeedLatencyCompensation() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return The intended compensation factor, which is always at least 1.
     */
    public static float getCompensationFactor(ServerPlayer player, ServerLevel level, BlockPos blockPos, BlockState blockState) {
        float intendedFactor = BlockBreakSpeedFactorUpdater.calculateFactor(player, level, blockPos, blockState);
        float highestFactor = player.serverToClientSideBlockBreakSpeedFactorHistory.getHighestValue();
        return Math.max(1, highestFactor / intendedFactor);
    }

}
