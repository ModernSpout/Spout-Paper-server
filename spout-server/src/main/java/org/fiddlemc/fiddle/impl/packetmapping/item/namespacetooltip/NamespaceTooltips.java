package org.fiddlemc.fiddle.impl.packetmapping.item.namespacetooltip;

import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.provider.configuration.PaperPluginMeta;
import io.papermc.paper.plugin.storage.ProviderStorage;
import io.papermc.paper.plugin.storage.SimpleProviderStorage;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;
import spout.common.branding.SpoutNamespace;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsImpl;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles namespace tooltips.
 */
public final class NamespaceTooltips {

    private NamespaceTooltips() {
        throw new UnsupportedOperationException();
    }

    private static final Map<String, String> namespaceNames = new HashMap<>();

    public static void registerNamespaceNames(ProviderStorage<?> storage) {
        if (storage instanceof SimpleProviderStorage<?> simpleStorage) {
            for (PluginProvider<?> provider : storage.getRegisteredProviders()) {
                if (provider.getMeta() instanceof PaperPluginMeta pluginMeta) {
                    if (pluginMeta.namespaces != null) {
                        namespaceNames.putAll(pluginMeta.namespaces);
                    }
                }
            }
        }
        if (SpoutGlobalConfiguration.get().tooltips.items.namespace) {
            ItemMappingsImpl.get().addEventInitializer(event -> {
                event.registerNMS(builder -> {
                    builder.everyAwarenessLevel();
                    builder.from(ItemRegistry.get().stream().filter(item -> {
                        if (item.isVanilla()) {
                            return false;
                        }
                        String namespace = item.keyInItemRegistry.getNamespace();
                        if (namespace.equals(Identifier.DEFAULT_NAMESPACE) || namespace.equals(SpoutNamespace.SPOUT)) {
                            return false;
                        }
                        return true;
                    }).toList());
                    builder.to(handle -> {
                        if (!SpoutGlobalConfiguration.get().tooltips.items.namespace) return;
                        String namespace = handle.getOriginal().getItem().keyInItemRegistry.getNamespace();
                        String name = getNamespaceNameOrNamespace(namespace);
                        Component nameComponent = Component.literal(name).withStyle(Style.EMPTY).withColor(NamedTextColor.BLUE.value());
                        ItemStack itemStack = handle.getMutable();
                        @Nullable ItemLore lore = itemStack.get(DataComponents.LORE);
                        if (lore == null) {
                            lore = new ItemLore(List.of(nameComponent));
                        } else {
                            lore = lore.withLineAdded(nameComponent);
                        }
                        itemStack.set(DataComponents.LORE, lore);
                    });
                });
            });
        }
    }

    public static @Nullable String getNamespaceNameOrNamespace(String namespace) {
        return namespaceNames.getOrDefault(namespace, namespace);
    }

}
