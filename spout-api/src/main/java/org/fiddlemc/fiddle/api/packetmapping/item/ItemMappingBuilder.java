package org.fiddlemc.fiddle.api.packetmapping.item;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.AwarenessLevelMappingBuilder;
import org.fiddlemc.fiddle.api.util.composable.FromBuilder;
import org.fiddlemc.fiddle.api.util.composable.FunctionBuilder;
import org.fiddlemc.fiddle.api.util.composable.ToBuilder;
import org.jspecify.annotations.Nullable;

/**
 * A builder to define an item mapping.
 */
public interface ItemMappingBuilder extends AwarenessLevelMappingBuilder, FromBuilder<ItemType>, ToBuilder<ItemType>, FunctionBuilder<ItemMappingHandle> {

    /**
     * Sets this builder to target all items.
     *
     * <p>
     * This negatively affects performance: try to target specific items instead.
     * </p>
     */
    default void fromAllItems() {
        this.from(Registry.ITEM.stream().toList());
    }

    /**
     * @return Whether this mapping should set the {@code item_model} component of the item stack,
     * to the {@link #itemModel()}.
     *
     * <p>
     * If null, it will be automatically treated as false for {@link ClientView.AwarenessLevel#VANILLA},
     * and true for all other {@link ClientView.AwarenessLevel}s.
     * </p>
     *
     * <p>
     * By default, this value is null.
     * </p>
     */
    @Nullable Boolean overrideItemModel();

    /**
     * Sets {@link #overrideItemModel()} to the given value.
     */
    void overrideItemModel(@Nullable Boolean setItemModel);

    /**
     * @return The item model to use to override the {@code item_model} component of the item stack,
     * {@linkplain #overrideItemModel() if it is enabled}.
     *
     * <p>
     * If null, it will be automatically set to the {@link ItemType#getKey()}
     * of the {@link ItemType} set with {@link #from}.
     * </p>
     *
     * <p>
     * By default, this value is null.
     * </p>
     */
    @Nullable NamespacedKey itemModel();

    /**
     * Sets {@link #itemModel()} to the given value.
     */
    void itemModel(@Nullable NamespacedKey itemModel);

}
