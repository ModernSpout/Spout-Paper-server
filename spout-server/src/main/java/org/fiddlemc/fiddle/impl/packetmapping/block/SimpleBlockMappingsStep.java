package org.fiddlemc.fiddle.impl.packetmapping.block;

import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link BlockMappingsStep} that always maps to a specific {@link BlockState}.
 */
public record SimpleBlockMappingsStep(BlockState to) implements BlockMappingsStep {

    @Override
    public void apply(BlockMappingHandleNMSImpl handle) {
        handle.set(this.to);
    }

}
