package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.impl.branding.FiddleNamespace;

/**
 * Analogous to {@link Registries}, but specifically for Fiddle registries populated from data packs.
 */
public final class FiddleDataPackRegistries {

    private FiddleDataPackRegistries() {
        throw new UnsupportedOperationException();
    }

    public static final ResourceKey<Registry<Block>> BLOCK_FROM_DATA_PACK = createRegistryKey("fiddle_block");
    public static final ResourceKey<Registry<Item>> ITEM_FROM_DATA_PACK = createRegistryKey("fiddle_item");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(FiddleNamespace.FIDDLE, name));
    }

}
