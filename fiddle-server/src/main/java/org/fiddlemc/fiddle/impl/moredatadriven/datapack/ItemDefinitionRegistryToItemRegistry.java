package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.impl.moredatadriven.datapack.beforefreeze.LoadBeforeFreezeBuiltInRegistries;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;

/**
 * Utility class that adds an {@link Item} entry to the {@link ItemRegistry}
 * for each {@link ItemDefinition} entry in the {@link FiddleDataPackResourceRegistries#ITEM_DEFINITION} registry.
 */
public final class ItemDefinitionRegistryToItemRegistry {

    private ItemDefinitionRegistryToItemRegistry() {
        throw new UnsupportedOperationException();
    }

    public static void apply() {
        LoadBeforeFreezeBuiltInRegistries.ITEM_DEFINITION.listElements().forEach(reference -> {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, reference.key().identifier());
            Item item = ItemDefinitionFromToItem.to(itemKey, reference.value());
            Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        });
    }

}
