package org.fiddlemc.fiddle.api.packetmapping.item.nms;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingsComposeEvent;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * An extension to {@link ItemMappingsComposeEvent} using Minecraft internals.
 */
public interface ItemMappingsComposeEventNMS<M> extends ItemMappingsComposeEvent<M> {

    /**
     * @see #register(Consumer)
     */
    void registerNMS(Consumer<ItemMappingBuilderNMS> builderConsumer);

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegisteredNMS(Pair<ClientView.AwarenessLevel, Item> key) {
        return this.getRegisteredNMS(key.left(), key.right());
    }

    /**
     * @see #getRegistered(ClientView.AwarenessLevel, ItemType)
     */
    default List<M> getRegisteredNMS(ClientView.AwarenessLevel awarenessLevel, Item from) {
        return this.getRegistered(awarenessLevel, Registry.ITEM.get(Key.key(from.keyInItemRegistry.getNamespace(), from.keyInItemRegistry.getPath())));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegisteredNMS(Pair<ClientView.AwarenessLevel, Item> key, Consumer<List<M>> listConsumer) {
        this.changeRegisteredNMS(key.left(), key.right(), listConsumer);
    }

    /**
     * @see #changeRegistered(ClientView.AwarenessLevel, ItemType, Consumer)
     */
    default void changeRegisteredNMS(ClientView.AwarenessLevel awarenessLevel, Item from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(awarenessLevel, Registry.ITEM.get(Key.key(from.keyInItemRegistry.getNamespace(), from.keyInItemRegistry.getPath())), listConsumer);
    }

    @Override
    default void register(Collection<ClientView.AwarenessLevel> awarenessLevels, ItemType from, ItemType to, @Nullable Boolean overrideItemModel, @Nullable NamespacedKey itemModel) {
        this.registerNMS(awarenessLevels, CraftItemType.bukkitToMinecraftNew(from), CraftItemType.bukkitToMinecraftNew(to), overrideItemModel, itemModel == null ? null : CraftNamespacedKey.toMinecraft(itemModel));
    }

    default void registerNMS(Item from, Item to) {
        this.registerNMS(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems(), from, to, null);
    }

    default void registerNMS(Item from, Item to, @Nullable Boolean overrideItemModel) {
        this.registerNMS(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems(), from, to, overrideItemModel, null);
    }

    default void registerNMS(Item from, Item to, @Nullable Boolean overrideItemModel, @Nullable Identifier itemModel) {
        this.registerNMS(List.of(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems()), from, to, overrideItemModel, itemModel);
    }

    default void registerNMS(ClientView.AwarenessLevel awarenessLevel, Item from, Item to) {
        this.registerNMS(awarenessLevel, from, to, null);
    }

    default void registerNMS(ClientView.AwarenessLevel awarenessLevel, Item from, Item to, @Nullable Boolean overrideItemModel) {
        this.registerNMS(awarenessLevel, from, to, overrideItemModel, null);
    }

    default void registerNMS(ClientView.AwarenessLevel awarenessLevel, Item from, Item to, @Nullable Boolean overrideItemModel, @Nullable Identifier itemModel) {
        this.registerNMS(List.of(awarenessLevel), from, to, overrideItemModel, itemModel);
    }

    default void registerNMS(ClientView.AwarenessLevel[] awarenessLevels, Item from, Item to) {
        this.registerNMS(awarenessLevels, from, to, null);
    }

    default void registerNMS(ClientView.AwarenessLevel[] awarenessLevels, Item from, Item to, @Nullable Boolean overrideItemModel) {
        this.registerNMS(awarenessLevels, from, to, overrideItemModel, null);
    }

    default void registerNMS(ClientView.AwarenessLevel[] awarenessLevels, Item from, Item to, @Nullable Boolean overrideItemModel, @Nullable Identifier itemModel) {
        this.registerNMS(Arrays.asList(awarenessLevels), from, to, overrideItemModel, itemModel);
    }

    default void registerNMS(Collection<ClientView.AwarenessLevel> awarenessLevels, Item from, Item to) {
        this.registerNMS(awarenessLevels, from, to, null);
    }

    default void registerNMS(Collection<ClientView.AwarenessLevel> awarenessLevels, Item from, Item to, @Nullable Boolean overrideItemModel) {
        this.registerNMS(awarenessLevels, from, to, overrideItemModel, null);
    }

    void registerNMS(Collection<ClientView.AwarenessLevel> awarenessLevels, Item from, Item to, @Nullable Boolean overrideItemModel, @Nullable Identifier itemModel);

}
