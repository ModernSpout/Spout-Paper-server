package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.fiddlemc.fiddle.impl.branding.FiddleNamespace;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition.BlockDefinition;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition.ItemDefinition;

/**
 * Analogous to {@link Registries}, but specifically for Fiddle registries populated from data packs.
 */
public final class FiddleDataPackRegistries {

    private FiddleDataPackRegistries() {
        throw new UnsupportedOperationException();
    }

    public static final ResourceKey<Registry<BlockDefinition>> BLOCK_DEFINITION = createRegistryKey("fiddle_block");
    public static final ResourceKey<Registry<ItemDefinition>> ITEM_DEFINITION = createRegistryKey("fiddle_item");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(FiddleNamespace.FIDDLE, name));
    }

}
