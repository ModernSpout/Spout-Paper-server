package org.fiddlemc.fiddle.impl.packetmapping.item;

import net.kyori.adventure.key.Key;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingUtilitiesNMS;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingsComposeEventNMS;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;
import org.fiddlemc.fiddle.impl.util.composable.AwarenessLevelPairKeyedBuilderComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * The implementation of {@link ItemMappingsComposeEvent}.
 */
public final class ItemMappingsComposeEventImpl extends AwarenessLevelPairKeyedBuilderComposeEventImpl<ItemType, ItemMappingsStep, ItemMappingBuilder> implements ItemMappingsComposeEventNMS<ItemMappingsStep> {

    @Override
    public void register(Consumer<ItemMappingBuilder> builderConsumer) {
        ItemMappingBuilderImpl builder = new ItemMappingBuilderImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    public void registerNMS(Consumer<ItemMappingBuilderNMS> builderConsumer) {
        ItemMappingBuilderNMSImpl builder = new ItemMappingBuilderNMSImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    public void registerNMS(Collection<ClientView.AwarenessLevel> awarenessLevels, Item from, Item to, @Nullable Boolean overrideItemModel, @Nullable Identifier itemModel) {
        this.registerNMS(builder -> {
            builder.awarenessLevel(awarenessLevels);
            builder.from(from);
            builder.to(to);
            builder.overrideItemModel(overrideItemModel);
            builder.itemModel(itemModel);
        });
    }

    @Override
    protected int keyPartToInt(ItemType key) {
        return ((CraftItemType<?>) key).getHandle().indexInItemRegistry;
    }

    @Override
    protected ItemType intToKeyPart(int internalKey) {
        Identifier identifier = ItemRegistry.get().byId(internalKey).keyInItemRegistry;
        return Registry.ITEM.get(Key.key(identifier.getNamespace(), identifier.getPath()));
    }

}
