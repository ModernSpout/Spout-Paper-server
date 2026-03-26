package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.BlockEntityTypeRegistryEntryBuilderNMS;
import org.jspecify.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;

public class BlockEntityTypeRegistryEntryImpl {
    protected @Nullable BlockEntityType<?> internal;

    public BlockEntityTypeRegistryEntryImpl(
        Conversions conversions,
        @Nullable BlockEntityType<?> internal
    ) {
        this.internal = internal;
    }

    public static final class BuilderImpl implements BlockEntityTypeRegistryEntryBuilderNMS, PaperRegistryBuilder<net.minecraft.world.level.block.entity.BlockEntityType<?>, org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.BlockEntityType> {
        private BlockEntityType.@Nullable BlockEntitySupplier<?> factory;
        private final Set<Block> validBlocks = new HashSet<>();

        public BuilderImpl(Conversions conversions, @Nullable BlockEntityType<?> internal) {}

        @Override
        public BlockEntityTypeRegistryEntryBuilderNMS factorNMS(BlockEntityType.BlockEntitySupplier<?> factory) {
            this.factory = factory;
            return this;
        }

        @Override
        public BlockEntityTypeRegistryEntryBuilderNMS validBlocksNMS(final Block... blocks) {
            this.validBlocks.addAll(Set.of(blocks));
            return this;
        }

        @Override
        public BlockEntityType<?> build() {
            if (this.factory == null) {
                throw new IllegalStateException("Factory must be set before building a BlockEntityType.");
            }
            return new BlockEntityType<>(this.factory, this.validBlocks);
        }
    }
}
