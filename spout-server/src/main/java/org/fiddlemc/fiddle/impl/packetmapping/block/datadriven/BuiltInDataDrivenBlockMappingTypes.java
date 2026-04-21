package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
import spout.common.branding.SpoutNamespace;
import org.fiddlemc.fiddle.impl.clientview.ClientViewImpl;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Some built-in {@link DataDrivenBlockMappingType}s.
 */
public final class BuiltInDataDrivenBlockMappingTypes {

    private BuiltInDataDrivenBlockMappingTypes() {
        throw new UnsupportedOperationException();
    }

    public static abstract class BuiltInDataDrivenBlockMappingType implements DataDrivenBlockMappingType {

        private BuiltInDataDrivenBlockMappingType(String key) {
            DataDrivenBlockMappingTypeRegistry.register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, key), this);
        }

    }

    public static <T> Collection<? extends BlockData> parseFromBlockStates(@Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
        T fromInput = mapLike.get("from");
        if (fromInput != null) {
            List<String> fromStrings;
            DataResult<String> fromSingleString = ops.getStringValue(fromInput);
            if (fromSingleString.isSuccess()) {
                fromStrings = List.of(fromSingleString.getOrThrow());
            } else {
                fromStrings = Codec.list(Codec.STRING).decode(ops, fromInput).getOrThrow().getFirst();
            }
            return fromStrings.stream().map(Bukkit::createBlockData).toList();
        } else if (block != null) {
            return CraftBlockType.minecraftToBukkitNew(block).createBlockDataStates();
        } else {
            throw new IllegalArgumentException("Missing 'from' in included in data-driven block mapping");
        }
    }

    public static <T> @Nullable BlockData[] parseFromBlockState(FromBlockStateRequestBuilder<?> builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockData @Nullable [] cached) {
        @Nullable BlockData parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T input = mapLike.get("from");
            if (input != null) {
                parsed = Bukkit.createBlockData(ops.getStringValue(input).getOrThrow());
            } else if (block != null) {
                parsed = block.defaultBlockState().createCraftBlockData();
            } else {
                parsed = null;
            }
            cached = new @Nullable BlockData[]{parsed};
        }
        if (parsed != null) {
            builder.from(parsed);
        }
        return cached;
    }

    public static <T> @Nullable BlockData[] parseToBlockState(ToBlockStateRequestBuilder<?> builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockData @Nullable [] cached) {
        @Nullable BlockData parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T input = mapLike.get("fallback");
            if (input != null) {
                parsed = Bukkit.createBlockData(ops.getStringValue(input).getOrThrow());
            } else {
                parsed = null;
            }
            cached = new @Nullable BlockData[]{parsed};
        }
        if (parsed != null) {
            builder.fallback(parsed);
        }
        return cached;
    }

    public static <T> @Nullable BlockType[] parseFromBlockType(FromBlockTypeRequestBuilder<?> builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockType @Nullable [] cached) {
        @Nullable BlockType parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T input = mapLike.get("from");
            if (input != null) {
                parsed = CraftBlockType.minecraftToBukkitNew(BlockRegistry.get().getValue(Identifier.CODEC.parse(ops, input).getOrThrow()));
            } else {
                parsed = CraftBlockType.minecraftToBukkitNew(block);
            }
            cached = new @Nullable BlockType[]{parsed};
        }
        if (parsed != null) {
            builder.from(parsed);
        }
        return cached;
    }

    public static <T> @Nullable BlockType[] parseToBlockType(ToBlockTypeRequestBuilder<?> builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockType @Nullable [] cached) {
        @Nullable BlockType parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T fallbackinput = mapLike.get("fallback");
            if (fallbackinput != null) {
                parsed = CraftBlockType.minecraftToBukkitNew(BlockRegistry.get().getValue(Identifier.CODEC.parse(ops, fallbackinput).getOrThrow()));
            } else {
                parsed = null;
            }
            cached = new @Nullable BlockType[]{parsed};
        }
        if (parsed != null) {
            builder.fallback(parsed);
        }
        return cached;
    }

    public static final DataDrivenBlockMappingType MANUAL = new BuiltInDataDrivenBlockMappingType("manual") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            Collection<? extends BlockData> fromStates = parseFromBlockStates(block, ops, mapLike);
            @Nullable List<ClientView.AwarenessLevel> awarenessLevels;
            T awarenessLevelsInput = mapLike.get("awareness_levels");
            if (awarenessLevelsInput != null) {
                awarenessLevels = ClientViewImpl.AWARENESS_LEVEL_LIST_CODEC.decode(ops, awarenessLevelsInput).getOrThrow().getFirst();
            } else {
                awarenessLevels = null;
            }
            @Nullable BlockState to;
            T toInput = mapLike.get("to");
            if (toInput != null) {
                to = ((CraftBlockData) Bukkit.createBlockData(ops.getStringValue(toInput).getOrThrow())).getState();
            } else {
                to = null;
            }
            for (BlockData fromState : fromStates) {
                event.manualMappings().registerNMS(builder -> {
                    if (awarenessLevels != null) {
                        builder.awarenessLevel(awarenessLevels);
                    }
                    builder.from(((CraftBlockData) fromState).getState());
                    if (to != null) {
                        builder.to(to);
                    }
                });
            }
        }

    };

    public static final DataDrivenBlockMappingType FULL_BLOCK = new BuiltInDataDrivenBlockMappingType("full_block") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            Collection<? extends BlockData> fromStates = parseFromBlockStates(block, ops, mapLike);
            @Nullable BlockData @Nullable [][] cachedTo = {null};
            for (BlockData fromState : fromStates) {
                event.automaticMappings().fullBlock(builder -> {
                    builder.from(fromState);
                    cachedTo[0] = parseToBlockState(builder, block, ops, mapLike, cachedTo[0]);
                });
            }
        }

    };

    public static final DataDrivenBlockMappingType LEAVES = new BuiltInDataDrivenBlockMappingType("leaves") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            event.automaticMappings().leaves(builder -> {
                parseFromBlockType(builder, block, ops, mapLike, null);
                parseToBlockType(builder, block, ops, mapLike, null);
                T tintedInput = mapLike.get("tinted");
                if (tintedInput != null) {
                    builder.tinted(ops.getBooleanValue(tintedInput).getOrThrow());
                }
            });
        }

    };

    public static final DataDrivenBlockMappingType SLAB = new BuiltInDataDrivenBlockMappingType("slab") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            event.automaticMappings().slab(builder -> {
                parseFromBlockType(builder, block, ops, mapLike, null);
                parseToBlockType(builder, block, ops, mapLike, null);
                T fullBlockFallbackinput = mapLike.get("full_block_fallback");
                if (fullBlockFallbackinput != null) {
                    builder.fullBlockFallback(Bukkit.createBlockData(ops.getStringValue(fullBlockFallbackinput).getOrThrow()));
                }
            });
        }

    };

    public static final DataDrivenBlockMappingType STAIRS = new BuiltInDataDrivenBlockMappingType("stairs") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            event.automaticMappings().stairs(builder -> {
                parseFromBlockType(builder, block, ops, mapLike, null);
                parseToBlockType(builder, block, ops, mapLike, null);
            });
        }

    };

    private static boolean bootstrapped = false;

    static void bootstrapIfNecessary() {
        if (!bootstrapped) {
            List.of(STAIRS);
            bootstrapped = true;
        }
    }

}
