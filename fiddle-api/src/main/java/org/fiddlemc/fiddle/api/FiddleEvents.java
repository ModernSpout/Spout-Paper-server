package org.fiddlemc.fiddle.api;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.BukkitEnumNamesComposeEvent;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.material.MaterialEnumNames;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.BlockRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.ItemRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.BlockEntityType;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.BlockEntityTypeRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleBlockTypeRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.FiddleItemTypeRegistryEntry;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappings;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappings;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslationsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappings;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingsComposeEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructFinishEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstruction;
import org.fiddlemc.fiddle.api.util.composable.ComposableEventType;
import org.jspecify.annotations.Nullable;

/**
 * A convenience class providing links to the different Fiddle {@link LifecycleEventType}s.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class FiddleEvents {

    private FiddleEvents() {
        throw new UnsupportedOperationException();
    }

    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<?, FiddleBlockTypeRegistryEntry.Builder>> BLOCK_TYPE = (LifecycleEventType.Prioritizable) RegistryEvents.BLOCK_TYPE.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<?, FiddleItemTypeRegistryEntry.Builder>> ITEM_TYPE = (LifecycleEventType.Prioritizable) RegistryEvents.ITEM_TYPE.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<BlockType, BlockRegistryEntry.Builder>> BLOCK = RegistryEvents.BLOCK.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<ItemType, ItemRegistryEntry.Builder>> ITEM = RegistryEvents.ITEM.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<BlockEntityType, BlockEntityTypeRegistryEntry.Builder>> BLOCK_ENTITY_TYPE = RegistryEvents.BLOCK_ENTITY_TYPE.compose();
    public static final ComposableEventType<FiddleResourcePackConstructEvent> RESOURCE_PACK_CONSTRUCT = FiddleResourcePackConstruction.get().compose();
    public static final LifecycleEventType<BootstrapContext, FiddleResourcePackConstructFinishEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> RESOURCE_PACK_CONSTRUCT_FINISH = FiddleResourcePackConstruction.get().finish();
    public static final ComposableEventType<BlockMappingsComposeEvent<?>> BLOCK_MAPPING = ((BlockMappings) BlockMappings.get()).compose();
    public static final ComposableEventType<ItemMappingsComposeEvent<?>> ITEM_MAPPING = ((ItemMappings) ItemMappings.get()).compose();
    public static final ComposableEventType<ComponentMappingsComposeEvent<?>> COMPONENT_MAPPING = ((ComponentMappings) ComponentMappings.get()).compose();
    public static final ComposableEventType<ServerSideTranslationsComposeEvent> SERVER_SIDE_TRANSLATION = ServerSideTranslations.get().compose();
    public static final ComposableEventType<BukkitEnumNamesComposeEvent<Triple<NamespacedKey, @Nullable BlockType, @Nullable ItemType>>> MATERIAL_ENUM_NAME = MaterialEnumNames.get().compose();

}
