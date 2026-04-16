package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import com.google.gson.JsonObject;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.world.level.block.state.BlockState;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import org.fiddlemc.fiddle.impl.resourcepack.plugin.discover.FiddlePluginResourcePackDiscoveryImpl;

/**
 * Common interface for {@link FromToBlockStateRequestBuilderImpl}
 * and {@link FromToBlockTypeRequestBuilderImpl}.
 */
public interface FromToBlockStatesRequestBuilder {

    BlockState[] fromStates();

    BlockState[] fallbackStates();

    default JsonObject getBlockstatesVariant(int fromStateI, PluginBootstrap bootstrap) {
        BlockState fromState = this.fromStates()[fromStateI];
        Blockstates blockstates = FiddlePluginResourcePackDiscoveryImpl.get().getResourcePackBlockstates(bootstrap, fromState.getBlock().keyInBlockRegistry);
        return blockstates.getVariant(fromState.createCraftBlockData());
    }

}
