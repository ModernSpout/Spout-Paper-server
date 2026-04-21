package org.fiddlemc.fiddle.impl.packetmapping.block.breakspeed;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingFunctionContextImpl;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingFunctionContextImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsImpl;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Set;

/**
 * A utility class to update {@link ServerPlayer#serverToClientSideBlockBreakSpeedFactor}.
 */
public final class BlockBreakSpeedFactorUpdater {

    private BlockBreakSpeedFactorUpdater() {
        throw new UnsupportedOperationException();
    }

    private static final float MAX_FACTOR = 10f; // TODO make dependent on ping: lower ping means higher allowed max factor (since less risk of overshooting after breaking)
    private static final float MIN_FACTOR = 0.0001f;

    public static void setFactorAndSendPacket(ServerPlayer player, float factor) {
        player.serverToClientSideBlockBreakSpeedFactorHistory.storeNewValue(player, factor);
        // Skip sending a packet if already the same
        if (player.serverToClientSideBlockBreakSpeedFactor == factor) {
            return;
        }
        player.serverToClientSideBlockBreakSpeedFactor = factor;
        player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), Set.of(player.getAttribute(Attributes.BLOCK_BREAK_SPEED)), player));
    }

    private static @Nullable BlockPos getTargetBlockPos(ServerPlayer player, ServerLevel level) {
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 lookVec = player.getViewVector(1.0f);
        Vec3 end = eyePos.add(lookVec.scale(player.blockInteractionRange() + 2)); // Add some range as margin
        ClipContext clipcontext = new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult blockHitResult = level.clip(clipcontext);
        return blockHitResult.getType() == HitResult.Type.BLOCK ? blockHitResult.getBlockPos() : null;
    }

    private static BlockState getTargetBlockState(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockStateIfLoaded(pos);
        return state != null ? state : Blocks.VOID_AIR.defaultBlockState();
    }

    public static float calculateFactor(ServerPlayer player) {

        // Get the server targeted block state
        ServerLevel level = player.level();
        @Nullable BlockPos blockPos = getTargetBlockPos(player, level);
        if (blockPos == null) {
            // Not targeting any block
            return 1;
        }
        BlockState serverBlockState = getTargetBlockState(level, blockPos);

        // Perform the rest of the calculation
        return calculateFactor(player, level, blockPos, serverBlockState);

    }

    public static float calculateFactor(ServerPlayer player, ServerLevel level, BlockPos blockPos, BlockState serverBlockState) {

        // Get the client targeted block state
        ClientView clientView = player.getClientViewOrFallback();
        BlockState clientBlockState = BlockMappingsImpl.get().apply(serverBlockState, new BlockMappingFunctionContextImpl(clientView, blockPos));

        // Get the server and client held item stack
        ItemStack serverItemStack = player.getInventory().getSelectedItem();
        ItemStack clientItemStack = ItemMappingsImpl.get().apply(serverItemStack, new ItemMappingFunctionContextImpl(clientView, false, false));

        // Compute the server and client destroy progress
        float serverDestroyProgress = serverBlockState.getDestroyProgress(player, level, blockPos);
        float clientDestroyProgress = clientBlockState.getDestroyProgress(player, level, blockPos, clientItemStack);
        boolean serverDestroyProgressIsInstant = Float.isNaN(serverDestroyProgress) || serverDestroyProgress > 0 && Float.isInfinite(serverDestroyProgress);
        boolean clientDestroyProgressIsInstant = Float.isNaN(clientDestroyProgress) || clientDestroyProgress > 0 && Float.isInfinite(clientDestroyProgress);
        if (serverDestroyProgressIsInstant) {
            // The block is instabreak server-side
            if (clientDestroyProgressIsInstant) {
                // The client knows
                return 1;
            } else {
                // Just set a high factor
                return MAX_FACTOR;
            }
        } else if (clientDestroyProgressIsInstant) {
            // The client thinks it's instabreak: this shouldn't happen if plugins choose their mappings properly, all we can do now is set a slow factor and pray it does something
            return MIN_FACTOR;
        }
        if (serverDestroyProgress == 0) {
            // The block cannot be broken server-side
            if (clientDestroyProgress == 0) {
                // The client knows
                return 1;
            }
            // We return a very small number
            return MIN_FACTOR;
        } else if (clientDestroyProgress == 0) {
            // The client thinks the block cannot be broken: this shouldn't happen if plugins choose their mappings properly, we can not do anything about it
            return 1;
        }

        // Compute the desired factor
        float factor = serverDestroyProgress / clientDestroyProgress;

        // Fallback in case of invalid results (like division by zero)
        if (Float.isNaN(factor)) {
            return 1;
        }
        // Don't return a crazy small factor
        if (factor < MIN_FACTOR) {
            return MIN_FACTOR;
        }
        // Don't return a large factor
        if (factor > MAX_FACTOR) {
            return MAX_FACTOR;
        }
        // Just return 1 if the factor is close enough
        if (0.999 <= factor && factor <= 1.001) {
            return 1;
        }

        // Return the factor
        return factor;

    }

    public static void updateFactor(ServerPlayer player) {
        float factor = calculateFactor(player);
        setFactorAndSendPacket(player, factor);
    }

    public static void updateFactor(List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            updateFactor(player);
        }
    }

}
