package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

/**
 * A base implementation of {@link FromBlockStateRequestBuilder} and {@link ToBlockStateRequestBuilder}.
 */
public class FromToBlockStateRequestBuilderImpl<US extends UsedStates> extends FromToItemRequestBuilderImpl<US> implements FromBlockStateRequestBuilder<US>, ToBlockStateRequestBuilder<US>, FromToBlockStatesRequestBuilder {

    public @Nullable BlockState from;
    public BlockState fallback;

    private BlockState @Nullable [] fromStates;
    private BlockState @Nullable [] fallbackStates;

    public FromToBlockStateRequestBuilderImpl() {
        super();
    }

    @Override
    public void from(BlockData from) {
        this.from = ((CraftBlockData) from).getState();
        this.fromStates = null;
    }

    @Override
    public @Nullable BlockData from() {
        return this.from == null ? null : this.from.createCraftBlockData();
    }

    @Override
    protected @Nullable Block getBlockToInferFromItem() {
        return this.from == null ? null : this.from.getBlock();
    }

    @Override
    public void fallback(BlockData fallback) {
        this.fallback = ((CraftBlockData) fallback).getState();
        this.fallbackStates = null;
    }

    @Override
    public BlockData fallback() {
        return this.fallback.createCraftBlockData();
    }

    @Override
    protected @Nullable Block getBlockToInferFallbackItem() {
        return this.fallback.getBlock();
    }

    protected void verifyFromNotNull() {
        Objects.requireNonNull(this.from, "Cannot process claim request: from is null");
    }

    @Override
    public void validateArguments() {
        this.verifyFromNotNull();
        super.validateArguments();
    }

    @Override
    public BlockState[] fromStates() {
        if (this.fromStates == null) {
            this.verifyFromNotNull();
            this.fromStates = new BlockState[]{this.from};
        }
        return this.fromStates;
    }

    @Override
    public BlockState[] fallbackStates() {
        if (this.fallbackStates == null) {
            this.fallbackStates = new BlockState[]{this.fallback};
        }
        return this.fallbackStates;
    }

}
