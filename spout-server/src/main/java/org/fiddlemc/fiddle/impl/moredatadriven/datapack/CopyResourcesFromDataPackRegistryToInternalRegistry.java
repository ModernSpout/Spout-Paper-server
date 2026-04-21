package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import java.util.stream.Stream;

/**
 * Provides functionality for adding resources based on definitions.
 */
public final class CopyResourcesFromDataPackRegistryToInternalRegistry {

    private CopyResourcesFromDataPackRegistryToInternalRegistry() {
        throw new UnsupportedOperationException();
    }

    public static <T extends DelayedResourceKeyAttachable<T>> void copy(Stream<Pair<Identifier, T>> keyedResources, Registry<T> internalRegistry) {
        keyedResources.forEach(keyedDefinition -> {
            ResourceKey<T> key = ResourceKey.create(internalRegistry.key(), keyedDefinition.left());
            T resource = keyedDefinition.right();
            resource.attachResourceKey(key);
            Registry.register(internalRegistry, key, resource);
        });
    }

    public static <T extends DelayedResourceKeyAttachable<T>> void copy(Registry<T> dataPackRegistry, Registry<T> internalRegistry) {
        copy(
            dataPackRegistry.listElements().map(element -> Pair.of(element.key().identifier(), element.value())),
            internalRegistry
        );
    }

    public interface DelayedResourceKeyAttachable<T> {

        void attachResourceKey(ResourceKey<T> resourceKey);

    }

}
