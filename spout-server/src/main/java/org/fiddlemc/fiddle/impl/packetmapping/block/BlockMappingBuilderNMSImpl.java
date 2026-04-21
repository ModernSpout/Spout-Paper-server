package org.fiddlemc.fiddle.impl.packetmapping.block;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingHandleNMS;
import java.util.Collection;

/**
 * The implementation of {@link BlockMappingBuilderNMS}.
 */
public class BlockMappingBuilderNMSImpl extends AbstractBlockMappingBuilderImpl<BlockState, BlockMappingHandleNMS> implements BlockMappingBuilderNMS {

    @Override
    protected Collection<BlockData> getStatesToRegisterFor() {
        return this.from.stream().map(state -> (BlockData) state.createCraftBlockData()).toList();
    }

    @Override
    protected BlockMappingsStep createFunctionStep() {
        return new MinecraftFunctionBlockMappingsStep(this.function, this.functionRequiresCoordinates);
    }

    @Override
    protected BlockState getSimpleTo() {
        return this.to;
    }

}
