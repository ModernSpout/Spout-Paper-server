package org.fiddlemc.fiddle.impl.moredatadriven.datapack.delayedfrozenregistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.BlockTypes;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.CopyResourcesFromDataPackRegistryToInternalRegistry;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.FiddleDataPackRegistries;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type.ItemTypes;
import java.util.List;
import java.util.function.Consumer;

/**
 * A holder, similar to {@link RegistryDataLoader#WORLDGEN_REGISTRIES},
 * for the registries that are loaded from data packs before the delayed registries are frozen.
 */
public final class DataPackRegistriesToLoadBeforeDelayedRegistryFreeze {

    private DataPackRegistriesToLoadBeforeDelayedRegistryFreeze() {
        throw new UnsupportedOperationException();
    }

    public static final List<Instance<?>> REGISTRIES = List.of(
        new Instance<>(
            FiddleDataPackRegistries.BLOCK_FROM_DATA_PACK,
            BlockTypes.CODEC,
            registry -> {
                CopyResourcesFromDataPackRegistryToInternalRegistry.copy(
                    registry,
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK
                );
            }
        ),
        new Instance<>(
            FiddleDataPackRegistries.ITEM_FROM_DATA_PACK,
            ItemTypes.CODEC,
            registry -> {
                CopyResourcesFromDataPackRegistryToInternalRegistry.copy(
                    registry,
                    net.minecraft.core.registries.BuiltInRegistries.ITEM
                );
            }
        )
    );

    public record Instance<T>(
        RegistryDataLoader.RegistryData<T> registryData,
        Consumer<Registry<T>> postLoadAction
    ) {

        public Instance(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Consumer<Registry<T>> postLoadAction) {
            this(new RegistryDataLoader.RegistryData<>(registryKey, codec, false), postLoadAction);
        }

        public Instance(ResourceKey<Registry<T>> registryKey, MapCodec<T> codec, Consumer<Registry<T>> postLoadAction) {
            this(registryKey, codec.codec(), postLoadAction);
        }

    }

}
