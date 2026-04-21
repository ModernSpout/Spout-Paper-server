package org.fiddlemc.fiddle.api.packetmapping.item.nms;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.AwarenessLevelMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingBuilder;
import org.fiddlemc.fiddle.api.util.composable.FromBuilder;
import org.fiddlemc.fiddle.api.util.composable.FunctionBuilder;
import org.fiddlemc.fiddle.api.util.composable.ToBuilder;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;
import org.jspecify.annotations.Nullable;

/**
 * An alternative to {@link ItemMappingBuilder} that uses Minecraft internals.
 */
public interface ItemMappingBuilderNMS extends AwarenessLevelMappingBuilder, FromBuilder<Item>, ToBuilder<Item>, FunctionBuilder<ItemMappingHandleNMS> {

    /**
     * Sets this builder to target all items.
     *
     * <p>
     * This negatively affects performance: try to target specific items instead.
     * </p>
     */
    default void fromAllItems() {
        this.from(ItemRegistry.get().stream().toList());
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
    void overrideItemModel(@Nullable Boolean overrideItemModel);

    /**
     * @return The item model to use to override the {@code item_model} component of the item stack,
     * {@linkplain #overrideItemModel() if it is enabled}.
     *
     * <p>
     * If null, it will be automatically set to the {@link Item#keyInItemRegistry}
     * of the {@link Item} set with {@link #from}.
     * </p>
     *
     * <p>
     * By default, this value is null.
     * </p>
     */
    @Nullable Identifier itemModel();

    /**
     * Sets {@link #itemModel()} to the given value.
     */
    void itemModel(@Nullable Identifier itemModel);

}
