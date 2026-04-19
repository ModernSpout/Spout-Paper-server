package org.fiddlemc.fiddle.api.packetmapping.item;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.util.composable.BuilderComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.ChangeRegisteredComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.GetRegisteredComposeEvent;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides functionality to register mappings to the {@link ItemMappings}.
 *
 * <p>
 * Mapping from {@linkplain ItemStack#isEmpty empty} to non-empty, or the other way around, will lead to glitches.
 * </p>
 *
 * <p>
 * Casting this instance to {@code ItemMappingsComposeEventNMS} and using its methods
 * with Minecraft internals gives <i>significantly</i> better performance.
 * </p>
 */
public interface ItemMappingsComposeEvent<M> extends BuilderComposeEvent<ItemMappingBuilder>, GetRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, ItemType>, M>, ChangeRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, ItemType>, M> {

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegistered(ClientView.AwarenessLevel awarenessLevel, ItemType from) {
        return this.getRegistered(Pair.of(awarenessLevel, from));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegistered(ClientView.AwarenessLevel awarenessLevel, ItemType from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(Pair.of(awarenessLevel, from), listConsumer);
    }

    default void register(ItemType from, ItemType to) {
        this.register(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems(), from, to, null);
    }

    default void register(ItemType from, ItemType to, @Nullable Boolean overrideItemModel) {
        this.register(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems(), from, to, overrideItemModel, null);
    }

    default void register(ItemType from, ItemType to, @Nullable Boolean overrideItemModel, @Nullable NamespacedKey itemModel) {
        this.register(List.of(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems()), from, to, overrideItemModel, itemModel);
    }

    default void register(ClientView.AwarenessLevel awarenessLevel, ItemType from, ItemType to) {
        this.register(awarenessLevel, from, to, null);
    }

    default void register(ClientView.AwarenessLevel awarenessLevel, ItemType from, ItemType to, @Nullable Boolean overrideItemModel) {
        this.register(awarenessLevel, from, to, overrideItemModel, null);
    }

    default void register(ClientView.AwarenessLevel awarenessLevel, ItemType from, ItemType to, @Nullable Boolean overrideItemModel, @Nullable NamespacedKey itemModel) {
        this.register(List.of(awarenessLevel), from, to, overrideItemModel, itemModel);
    }

    default void register(ClientView.AwarenessLevel[] awarenessLevels, ItemType from, ItemType to) {
        this.register(awarenessLevels, from, to, null);
    }

    default void register(ClientView.AwarenessLevel[] awarenessLevels, ItemType from, ItemType to, @Nullable Boolean overrideItemModel) {
        this.register(awarenessLevels, from, to, overrideItemModel, null);
    }

    default void register(ClientView.AwarenessLevel[] awarenessLevels, ItemType from, ItemType to, @Nullable Boolean overrideItemModel, @Nullable NamespacedKey itemModel) {
        this.register(Arrays.asList(awarenessLevels), from, to, overrideItemModel, itemModel);
    }

    default void register(Collection<ClientView.AwarenessLevel> awarenessLevels, ItemType from, ItemType to) {
        this.register(awarenessLevels, from, to, null);
    }

    default void register(Collection<ClientView.AwarenessLevel> awarenessLevels, ItemType from, ItemType to, @Nullable Boolean overrideItemModel) {
        this.register(awarenessLevels, from, to, overrideItemModel, null);
    }

    void register(Collection<ClientView.AwarenessLevel> awarenessLevels, ItemType from, ItemType to, @Nullable Boolean overrideItemModel, @Nullable NamespacedKey itemModel);

}
