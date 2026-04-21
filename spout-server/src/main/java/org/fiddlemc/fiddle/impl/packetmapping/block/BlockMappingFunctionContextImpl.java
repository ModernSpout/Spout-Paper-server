package org.fiddlemc.fiddle.impl.packetmapping.block;

import net.minecraft.core.BlockPos;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;
import org.fiddlemc.fiddle.impl.packetmapping.WithClientViewMappingFunctionContextImpl;

/**
 * The implementation of {@link BlockMappingFunctionContext}.
 */
public class BlockMappingFunctionContextImpl extends WithClientViewMappingFunctionContextImpl implements BlockMappingFunctionContext {

    private final boolean isStateOfPhysicalBlockInWorld;
    private final int physicalBlockX;
    private final int physicalBlockY;
    private final int physicalBlockZ;

    public BlockMappingFunctionContextImpl(ClientView clientView, boolean isStateOfPhysicalBlockInWorld, int physicalBlockX, int physicalBlockY, int physicalBlockZ) {
        super(clientView);
        this.isStateOfPhysicalBlockInWorld = isStateOfPhysicalBlockInWorld;
        this.physicalBlockX = physicalBlockX;
        this.physicalBlockY = physicalBlockY;
        this.physicalBlockZ = physicalBlockZ;
    }

    public BlockMappingFunctionContextImpl(ClientView clientView) {
        this(clientView, false, 0, 0, 0);
    }

    public BlockMappingFunctionContextImpl(ClientView clientView, int physicalBlockX, int physicalBlockY, int physicalBlockZ) {
        this(clientView, true, physicalBlockX, physicalBlockY, physicalBlockZ);
    }

    public BlockMappingFunctionContextImpl(ClientView clientView, BlockPos physicalBlockPos) {
        this(clientView, physicalBlockPos.getX(), physicalBlockPos.getY(), physicalBlockPos.getZ());
    }

    @Override
    public boolean isStateOfPhysicalBlockInWorld() {
        return this.isStateOfPhysicalBlockInWorld;
    }

    @Override
    public int getPhysicalBlockX() {
        return this.physicalBlockX;
    }

    @Override
    public int getPhysicalBlockY() {
        return this.physicalBlockY;
    }

    @Override
    public int getPhysicalBlockZ() {
        return this.physicalBlockZ;
    }

}
