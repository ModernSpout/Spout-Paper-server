package org.fiddlemc.fiddle.impl.packetmapping.item.datadriven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.impl.branding.FiddleNamespace;
import org.fiddlemc.fiddle.impl.clientview.ClientViewImpl;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.ItemRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.datadriven.DataDrivenBlockMappingType;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Some built-in {@link DataDrivenBlockMappingType}s.
 */
public final class BuiltInDataDrivenItemMappingTypes {

    private BuiltInDataDrivenItemMappingTypes() {
        throw new UnsupportedOperationException();
    }

    public static abstract class BuiltInDataDrivenItemMappingType implements DataDrivenItemMappingType {

        private BuiltInDataDrivenItemMappingType(String key) {
            DataDrivenItemMappingTypeRegistry.register(Identifier.fromNamespaceAndPath(FiddleNamespace.FIDDLE, key), this);
        }

    }

    public static <T> Collection<Item> parseFromItems(@Nullable Item item, DynamicOps<T> ops, MapLike<T> mapLike) {
        T fromInput = mapLike.get("from");
        if (fromInput != null) {
            List<String> fromStrings;
            DataResult<String> fromSingleString = ops.getStringValue(fromInput);
            if (fromSingleString.isSuccess()) {
                fromStrings = List.of(fromSingleString.getOrThrow());
            } else {
                fromStrings = Codec.list(Codec.STRING).decode(ops, fromInput).getOrThrow().getFirst();
            }
            return fromStrings.stream().map(string -> Objects.requireNonNull(ItemRegistry.get().getValue(Identifier.parse(string)))).toList();
        } else if (item != null) {
            return List.of(item);
        } else {
            throw new IllegalArgumentException("Missing 'from' in included in data-driven item mapping");
        }
    }

    public static final DataDrivenItemMappingType MANUAL = new BuiltInDataDrivenItemMappingType("manual") {

        @Override
        public <T> void apply(ItemMappingsComposeEventImpl event, @Nullable Item item, DynamicOps<T> ops, MapLike<T> mapLike) {
            Collection<Item> from = parseFromItems(item, ops, mapLike);
            List<ClientView.AwarenessLevel> awarenessLevels;
            T awarenessLevelsInput = mapLike.get("awareness_levels");
            if (awarenessLevelsInput != null) {
                awarenessLevels = ClientViewImpl.AWARENESS_LEVEL_LIST_CODEC.decode(ops, awarenessLevelsInput).getOrThrow().getFirst();
            } else {
                awarenessLevels = Arrays.asList(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems());
            }
            if (awarenessLevels.isEmpty()) {
                return;
            }
            @Nullable Item to;
            T toInput = mapLike.get("to");
            if (toInput != null) {
                to = Objects.requireNonNull(ItemRegistry.get().getValue(Identifier.CODEC.decode(ops, toInput).getOrThrow().getFirst()));
            } else {
                to = null;
            }
            @Nullable Boolean overrideItemModel;
            T overrideItemModelInput = mapLike.get("override_item_model");
            if (overrideItemModelInput != null) {
                overrideItemModel = ops.getBooleanValue(overrideItemModelInput).getOrThrow();
            } else {
                overrideItemModel = null;
            }
            @Nullable Identifier itemModel;
            T itemModelInput = mapLike.get("item_model");
            if (itemModelInput != null) {
                itemModel = Identifier.CODEC.decode(ops, itemModelInput).getOrThrow().getFirst();
            } else {
                itemModel = null;
            }
            event.registerNMS(builder -> {
                builder.from(from);
                builder.awarenessLevel(awarenessLevels);
                if (to != null) {
                    builder.to(to);
                }
                if (overrideItemModel != null) {
                    builder.overrideItemModel(overrideItemModel);
                }
                if (itemModel != null) {
                    builder.itemModel(itemModel);
                }
            });
        }

    };

    private static boolean bootstrapped = false;

    static void bootstrapIfNecessary() {
        if (!bootstrapped) {
            List.of(MANUAL);
            bootstrapped = true;
        }
    }

}
