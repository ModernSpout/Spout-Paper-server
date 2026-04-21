package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.LeavesRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link LeavesRequestBuilder}.
 */
public class LeavesRequestBuilderImpl extends FromToBlockTypeRequestBuilderImpl<UsedStates.Waterlogged> implements LeavesRequestBuilder {

    public @Nullable Boolean tinted;

    public LeavesRequestBuilderImpl() {
        super();
        this.fallback = Blocks.OAK_LEAVES;
    }

    @Override
    public void tinted(@Nullable Boolean tinted) {
        this.tinted = tinted;
    }

    @Override
    public @Nullable Boolean tinted() {
        return this.tinted;
    }

    public BlockState fallbackNonWaterloggedState() {
        return this.fallback.defaultBlockState();
    }

    public BlockState fallbackWaterloggedState() {
        return this.fallbackNonWaterloggedState().setValue(BlockStateProperties.WATERLOGGED, true);
    }

    public boolean tintedOrInfer() {
        return this.tinted != null ? this.tinted : this.from instanceof TintedParticleLeavesBlock;
    }

}
