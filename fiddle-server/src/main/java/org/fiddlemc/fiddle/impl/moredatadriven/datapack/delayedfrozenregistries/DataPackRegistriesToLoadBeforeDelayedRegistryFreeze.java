package org.fiddlemc.fiddle.impl.moredatadriven.datapack.delayedfrozenregistries;

import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.FiddleDataPackRegistries;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type.ItemTypes;
import java.util.List;

/**
 * A holder, similar to {@link RegistryDataLoader#WORLDGEN_REGISTRIES},
 * for the registries that are loaded from data packs before the delayed registries are frozen.
 */
public final class DataPackRegistriesToLoadBeforeDelayedRegistryFreeze {

    private DataPackRegistriesToLoadBeforeDelayedRegistryFreeze() {
        throw new UnsupportedOperationException();
    }

    public static final List<RegistryDataLoader.RegistryData<?>> REGISTRIES = List.of(
        new RegistryDataLoader.RegistryData<Block>(FiddleDataPackRegistries.BLOCK_FROM_DATA_PACK, BlockTypes.CODEC.codec(), false),
        new RegistryDataLoader.RegistryData<>(FiddleDataPackRegistries.ITEM_FROM_DATA_PACK, ItemTypes.CODEC.codec(), false)
    );

}
