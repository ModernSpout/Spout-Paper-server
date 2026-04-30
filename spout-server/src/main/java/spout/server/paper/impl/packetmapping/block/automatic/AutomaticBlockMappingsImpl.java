package spout.server.paper.impl.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.LeavesRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.AutomaticBlockMappings;
import spout.server.paper.api.packetmapping.block.automatic.SlabRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.function.Consumer;

/**
 * The implementation of {@link AutomaticBlockMappings}.
 */
public final class AutomaticBlockMappingsImpl implements AutomaticBlockMappings {

    private final BlockMappingsComposeEventImpl event;

    public AutomaticBlockMappingsImpl(BlockMappingsComposeEventImpl event) {
        this.event = event;
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder<UsedStates.Switch> & ToBlockTypeRequestBuilder<UsedStates.Switch>> void button(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl<UsedStates.Switch> builder = new FromToBlockTypeRequestBuilderImpl<>();
        builder.fallback(BlockType.STONE_BUTTON);
        ((Consumer) builderConsumer).accept(builder);
        new ButtonRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockStateRequestBuilder<UsedStates.Single> & ToBlockStateRequestBuilder<UsedStates.Single>> void fullBlock(Consumer<? extends B> builderConsumer) {
        FromToBlockStateRequestBuilderImpl<UsedStates.Single> builder = new FromToBlockStateRequestBuilderImpl<>();
        builder.fallbackDefaultStateOf(BlockType.STONE);
        ((Consumer) builderConsumer).accept(builder);
        new FullBlockRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends LeavesRequestBuilder> void leaves(Consumer<? extends B> builderConsumer) {
        LeavesRequestBuilderImpl builder = new LeavesRequestBuilderImpl();
        builder.fallback(BlockType.OAK_LEAVES);
        ((Consumer) builderConsumer).accept(builder);
        new LeavesRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder<UsedStates.Powerable> & ToBlockTypeRequestBuilder<UsedStates.Powerable>> void pressurePlate(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl<UsedStates.Powerable> builder = new FromToBlockTypeRequestBuilderImpl<>();
        builder.fallback(BlockType.STONE_PRESSURE_PLATE);
        ((Consumer) builderConsumer).accept(builder);
        new PressurePlateRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends SlabRequestBuilder> void slab(Consumer<? extends B> builderConsumer) {
        SlabRequestBuilderImpl builder = new SlabRequestBuilderImpl();
        builder.fallback(BlockType.STONE_SLAB);
        ((Consumer) builderConsumer).accept(builder);
        new SlabRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder<UsedStates.Stairs> & ToBlockTypeRequestBuilder<UsedStates.Stairs>> void stairs(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl<UsedStates.Stairs> builder = new FromToBlockTypeRequestBuilderImpl<>();
        builder.fallback(BlockType.STONE_STAIRS);
        ((Consumer) builderConsumer).accept(builder);
        new StairsRequestProcessor(builder, this.event).process();
    }

}
