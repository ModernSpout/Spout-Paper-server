package org.fiddlemc.fiddle.impl.packetmapping.block;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingHandle;
import java.util.Collection;

/**
 * The implementation of {@link BlockMappingBuilder}.
 */
public class BlockMappingBuilderImpl extends AbstractBlockMappingBuilderImpl<BlockData, BlockMappingHandle> implements BlockMappingBuilder {

    @Override
    protected Collection<BlockData> getStatesToRegisterFor() {
        return this.from;
    }

    @Override
    protected BlockMappingsStep createFunctionStep() {
        return new BukkitFunctionBlockMappingsStep(this.function, this.functionRequiresCoordinates);
    }

    @Override
    protected BlockState getSimpleTo() {
        return ((CraftBlockData) this.to).getState();
    }

}
