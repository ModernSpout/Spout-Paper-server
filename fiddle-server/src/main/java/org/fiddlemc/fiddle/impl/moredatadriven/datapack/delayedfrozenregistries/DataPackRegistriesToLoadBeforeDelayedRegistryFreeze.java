package org.fiddlemc.fiddle.impl.moredatadriven.datapack.delayedfrozenregistries;

import net.minecraft.resources.RegistryDataLoader;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.FiddleDataPackRegistries;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition.BlockDefinition;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition.ItemDefinition;
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
        new RegistryDataLoader.RegistryData<>(FiddleDataPackRegistries.BLOCK_DEFINITION, BlockDefinition.CODEC, false),
        new RegistryDataLoader.RegistryData<>(FiddleDataPackRegistries.ITEM_DEFINITION, ItemDefinition.CODEC, false)
    );

}
