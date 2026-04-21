package org.fiddlemc.fiddle.impl.packetmapping.block;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingFunctionContext;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingHandle;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingHandleNMS;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.CrossMappedWithContextMappingFunctionHandleImpl;
import org.fiddlemc.fiddle.impl.util.mappingpipeline.SimpleWithContextMappingFunctionHandleImpl;
import org.jspecify.annotations.Nullable;

/**
 * The handle passed to {@link BlockMappingsStep}s.
 */
public class BlockMappingHandleNMSImpl extends SimpleWithContextMappingFunctionHandleImpl<BlockState, BlockState, BlockMappingFunctionContext> implements BlockMappingHandleNMS {

    /**
     * Cached return value for {@link #bukkitHandle()}.
     */
    private @Nullable BukkitHandle bukkitHandle;

    public BlockMappingHandleNMSImpl(BlockState data, BlockMappingFunctionContext context, boolean isDataMutable) {
        super(data, context, isDataMutable);
    }

    private static class BukkitHandle extends CrossMappedWithContextMappingFunctionHandleImpl<BlockData, BlockMappingFunctionContext, BlockState, BlockMappingHandleNMSImpl> implements BlockMappingHandle {

        public BukkitHandle(BlockMappingHandleNMSImpl internal) {
            super(internal);
        }

        @Override
        protected BlockState mapToInternal(BlockData data) {
            return ((CraftBlockData) data).getState();
        }

        @Override
        protected BlockData mapFromInternal(BlockState data) {
            return data.createCraftBlockData();
        }

    }

    /**
     * @return A handle that can be passed to {@link BukkitFunctionBlockMappingsStep}.
     */
    public BlockMappingHandle bukkitHandle() {
        if (this.bukkitHandle == null) {
            this.bukkitHandle = new BukkitHandle(this);
        }
        return this.bukkitHandle;
    }

}
