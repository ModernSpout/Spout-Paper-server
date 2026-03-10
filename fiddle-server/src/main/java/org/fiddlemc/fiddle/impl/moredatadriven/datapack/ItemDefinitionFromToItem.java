package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import java.util.Optional;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * A utility class for transforming {@link ItemDefinition} to {@link Item}
 * and the other way around.
 */
public final class ItemDefinitionFromToItem {

    private ItemDefinitionFromToItem() {
        throw new UnsupportedOperationException();
    }

    public static ItemDefinition from(Item item) {
        return new ItemDefinition(
            Optional.of(item.components())
        );
    }

    public static Item to(ResourceKey<Item> id, ItemDefinition definition) {
        Item.Properties properties = new Item.Properties();
        definition.components.ifPresent(components -> {
            components.forEach(component -> {
                copyComponentToProperties(properties, component);
            });
        });
        properties.setId(id);
        return new Item(properties);
    }

    private static <T> void copyComponentToProperties(Item.Properties properties, TypedDataComponent<T> component) {
        properties.component(component.type(), component.value());
    }

}
