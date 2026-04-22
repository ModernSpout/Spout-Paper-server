package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeImpl extends HolderableBase<BlockEntityType<?>> implements org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.BlockEntityType {

    protected BlockEntityTypeImpl(final Holder<BlockEntityType<?>> holder) {
        super(holder);
    }

    public static BlockEntityTypeImpl of(Holder<BlockEntityType<?>> holder) {
        return new BlockEntityTypeImpl(holder);
    }
}
