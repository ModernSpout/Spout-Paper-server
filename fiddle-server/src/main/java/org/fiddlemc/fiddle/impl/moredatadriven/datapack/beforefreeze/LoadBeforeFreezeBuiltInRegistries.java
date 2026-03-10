package org.fiddlemc.fiddle.impl.moredatadriven.datapack.beforefreeze;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.BlockDefinition;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.FiddleDataPackResourceRegistries;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.ItemDefinition;
import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * A holder for the registries that are loaded early,
 * similar to {@link RegistryDataLoader#WORLDGEN_REGISTRIES}.
 */
public final class LoadBeforeFreezeBuiltInRegistries {

    private LoadBeforeFreezeBuiltInRegistries() {
        throw new UnsupportedOperationException();
    }

    public static final List<RegistryDataLoader.RegistryData<?>> REGISTRIES = List.of(
        new RegistryDataLoader.RegistryData<>(FiddleDataPackResourceRegistries.BLOCK_DEFINITION, BlockDefinition.CODEC, false),
        new RegistryDataLoader.RegistryData<>(FiddleDataPackResourceRegistries.ITEM_DEFINITION, ItemDefinition.CODEC, false)
    );

    /**
     * The {@link BlockDefinition} registry, initialized after it is populated.
     */
    public static @Nullable Registry<BlockDefinition> BLOCK_DEFINITION;

    /**
     * The {@link ItemDefinition} registry, initialized after it is populated.
     */
    public static @Nullable Registry<ItemDefinition> ITEM_DEFINITION;

}
