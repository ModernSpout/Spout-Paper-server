package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import java.util.stream.Stream;

/**
 * Provides functionality for adding resources based on definitions.
 */
public final class AddResourcesForDefinitions {

    private AddResourcesForDefinitions() {
        throw new UnsupportedOperationException();
    }

    public static <T> void addDefinitionsToResourceRegistry(Stream<Pair<Identifier, Definition<T>>> keyedDefinitions, Registry<T> registry) {
        keyedDefinitions.forEach(keyedDefinition -> {
            ResourceKey<T> key = ResourceKey.create(registry.key(), keyedDefinition.left());
            T resource = keyedDefinition.right().toResource(key);
            Registry.register(registry, key, resource);
        });
    }

    public static <T> void addDefinitionsToResourceRegistry(Registry<Definition<T>> definitionRegistry, Registry<T> resourceRegistry) {
        addDefinitionsToResourceRegistry(
            definitionRegistry.listElements().map(element -> Pair.of(element.key().identifier(), element.value())),
            resourceRegistry
        );
    }

}
