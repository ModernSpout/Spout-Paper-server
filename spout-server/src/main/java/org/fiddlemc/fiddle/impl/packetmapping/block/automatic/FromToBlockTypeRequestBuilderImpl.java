package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

/**
 * A base implementation of {@link FromBlockTypeRequestBuilder} and {@link ToBlockTypeRequestBuilder}.
 */
public class FromToBlockTypeRequestBuilderImpl<US extends UsedStates> extends FromToItemRequestBuilderImpl<US> implements FromBlockTypeRequestBuilder<US>, ToBlockTypeRequestBuilder<US>, FromToBlockStatesRequestBuilder {

    public @Nullable Block from;
    public Block fallback;

    private BlockState @Nullable [] fromStates;
    private BlockState @Nullable [] fallbackStates;

    public FromToBlockTypeRequestBuilderImpl() {
        super();
        this.fallback = Blocks.STONE;
    }

    @Override
    public void from(BlockType from) {
        this.from = CraftBlockType.bukkitToMinecraftNew(from);
        this.fromStates = null;
        this.fallbackStates = null;
    }

    @Override
    public @Nullable BlockType from() {
        return CraftBlockType.minecraftToBukkitNew(this.from);
    }

    @Override
    protected @Nullable Block getBlockToInferFromItem() {
        return this.from;
    }

    @Override
    public void fallback(BlockType fallback) {
        this.fallback = CraftBlockType.bukkitToMinecraftNew(fallback);
        this.fallbackStates = null;
    }

    @Override
    public BlockType fallback() {
        return CraftBlockType.minecraftToBukkitNew(this.fallback);
    }

    @Override
    protected @Nullable Block getBlockToInferFallbackItem() {
        return this.fallback;
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
            this.fromStates = this.from.getStateDefinition().getPossibleStates().toArray(BlockState[]::new);
        }
        return this.fromStates;
    }

    @Override
    public BlockState[] fallbackStates() {
        if (this.fallbackStates == null) {
            BlockState fallbackDefaultState = this.fallback.defaultBlockState();
            BlockState[] fromStates = this.fromStates();
            this.fallbackStates = new BlockState[fromStates.length];
            for (int i = 0; i < fallbackStates.length; i++) {
                this.fallbackStates[i] = fallbackDefaultState;
                for (Property<?> property : fallbackDefaultState.getProperties()) {
                    if (fromStates[i].hasProperty(property)) {
                        this.fallbackStates[i] = this.fallbackStates[i].setValue((Property) property, fromStates[i].getValue(property));
                    }
                }
            }
        }
        return this.fallbackStates;
    }

}
