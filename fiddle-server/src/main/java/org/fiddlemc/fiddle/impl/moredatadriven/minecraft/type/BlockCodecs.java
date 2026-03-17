package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holder for codecs related to blocks.
 */
public final class BlockCodecs {

    private static @Nullable Map<Identifier, MapColor> MAP_COLOR_FROM_KEY;
    private static @Nullable Map<MapColor, Identifier> MAP_COLOR_TO_KEY;

    private static void initializeMapColors() {
        if (MAP_COLOR_FROM_KEY == null) {
            List<Field> fields = Arrays.stream(MapColor.class.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                .filter(field -> field.getType() == MapColor.class)
                .toList();
            MAP_COLOR_FROM_KEY = new HashMap<>(fields.size());
            MAP_COLOR_TO_KEY = new HashMap<>(fields.size());
            for (Field field : fields) {
                Identifier key = Identifier.parse(field.getName().toLowerCase(Locale.ROOT));
                MapColor mapColor;
                try {
                    field.trySetAccessible();
                    mapColor = (MapColor) field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                MAP_COLOR_FROM_KEY.put(key, mapColor);
                MAP_COLOR_TO_KEY.put(mapColor, key);
            }
        }
    }

    private static DataResult<MapColor> mapColorFromKey(Identifier key) {
        initializeMapColors();
        MapColor mapColor = MAP_COLOR_FROM_KEY.get(key);
        return mapColor != null ? DataResult.success(mapColor) : DataResult.error(() -> "Unknown map color: " + key);
    }


    private static Identifier mapColorToKey(MapColor mapColor) {
        initializeMapColors();
        return MAP_COLOR_TO_KEY.get(mapColor);
    }

    public static final Codec<MapColor> MAP_COLOR_CODEC = Identifier.CODEC.comapFlatMap(BlockCodecs::mapColorFromKey, BlockCodecs::mapColorToKey);

    public static final Codec<MapColorFunction.Single> SINGLE_MAP_COLOR_FUNCTION_CODEC = MAP_COLOR_CODEC.xmap(MapColorFunction.Single::new, MapColorFunction.Single::mapColor);

    public static final Codec<MapColorFunction.ByProperties> BY_PROPERTIES_MAP_COLOR_FUNCTION_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(MapColorFunction.ByProperties input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();
            input.listAllCombinations().stream().forEach(combination -> {
                builder.add(BlockStateStringConversion.propertyKeyValuesToString(combination.left()), combination.right(), MAP_COLOR_CODEC);
            });
            return builder.build(prefix);
        }

        @Override
        public <T> DataResult<Pair<MapColorFunction.ByProperties, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(mapLike -> {
                List<String> propertyNames;
                {
                    Pair<T, T> entry = mapLike.entries().findAny().get();
                    propertyNames = BlockStateStringConversion.propertyKeyValuesFromString(ops.getStringValue(entry.getFirst()).getOrThrow()).stream().map(it.unimi.dsi.fastutil.Pair::left).toList();
                }
                Map<List<String>, MapColor> precomputedMapColors = mapLike.entries().collect(Collectors.toMap(
                    entry -> BlockStateStringConversion.propertyKeyValuesFromString(ops.getStringValue(entry.getFirst()).getOrThrow()).stream().map(it.unimi.dsi.fastutil.Pair::right).toList(),
                    entry -> MAP_COLOR_CODEC.decode(ops, input).getOrThrow().getFirst())
                );
                return DataResult.success(Pair.of(new MapColorFunction.ByProperties(propertyNames, precomputedMapColors), input));
            });
        }

    };

    public static final Codec<MapColorFunction> MAP_COLOR_FUNCTION_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(MapColorFunction input, DynamicOps<T> ops, T prefix) {
            return switch (input) {
                case MapColorFunction.Single single -> SINGLE_MAP_COLOR_FUNCTION_CODEC.encode(single, ops, prefix);
                case MapColorFunction.ByProperties byProperties ->
                    BY_PROPERTIES_MAP_COLOR_FUNCTION_CODEC.encode(byProperties, ops, prefix);
            };
        }

        @Override
        public <T> DataResult<Pair<MapColorFunction, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<String> stringInput = ops.getStringValue(input);
            if (stringInput.isSuccess()) {
                return (DataResult) SINGLE_MAP_COLOR_FUNCTION_CODEC.decode(ops, input);
            }
            return (DataResult) BY_PROPERTIES_MAP_COLOR_FUNCTION_CODEC.decode(ops, input);
        }

    };

    public static final Codec<BlockBehaviour.Properties> PROPERTIES_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(BlockBehaviour.Properties input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("map_color", input.mapColor, MAP_COLOR_FUNCTION_CODEC);
            builder.add("has_collision", ops.createBoolean(input.hasCollision));
            if (input.wasExplosionResistanceSet) {
                builder.add("explosion_resistance", ops.createFloat(input.explosionResistance));
            }
            builder.add("destroy_time", ops.createFloat(input.destroyTime));
            builder.add("requires_correct_tool_for_drops", ops.createBoolean(input.requiresCorrectToolForDrops));
            builder.add("is_randomly_ticking", ops.createBoolean(input.isRandomlyTicking));
            builder.add("friction", ops.createFloat(input.friction));
            builder.add("speed_factor", ops.createFloat(input.speedFactor));
            builder.add("jump_factor", ops.createFloat(input.jumpFactor));
            builder.add("can_occlude", ops.createBoolean(input.canOcclude));
            builder.add("is_air", ops.createBoolean(input.isAir));
            builder.add("ignited_by_lava", ops.createBoolean(input.ignitedByLava));
            builder.add("liquid", ops.createBoolean(input.liquid));
            builder.add("force_solid_off", ops.createBoolean(input.forceSolidOff));
            builder.add("force_solid_on", ops.createBoolean(input.forceSolidOn));
            builder.add("spawn_terrain_particles", ops.createBoolean(input.spawnTerrainParticles));
            builder.add("replaceable", ops.createBoolean(input.replaceable));
            builder.add("dynamic_shape", ops.createBoolean(input.dynamicShape));
            return builder.build(prefix);
        }

        @Override
        public <T> DataResult<Pair<BlockBehaviour.Properties, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(mapLike -> {
                BlockBehaviour.Properties properties = new BlockBehaviour.Properties();
                T mapColorInput = mapLike.get("map_color");
                if (mapColorInput != null) {
                    DataResult<Pair<MapColorFunction, T>> mapColor = MAP_COLOR_FUNCTION_CODEC.decode(ops, mapColorInput);
                    if (mapColor.isError()) {
                        return mapColor.map($ -> null);
                    }
                    properties.mapColor = mapColor.getOrThrow().getFirst();
                }
                T hasCollisionInput = mapLike.get("has_collision");
                if (hasCollisionInput != null) {
                    DataResult<Boolean> hasCollision = ops.getBooleanValue(hasCollisionInput);
                    if (hasCollision.isError()) {
                        return hasCollision.map($ -> null);
                    }
                    properties.hasCollision = hasCollision.getOrThrow();
                }
                T explosionResistanceInput = mapLike.get("explosion_resistance");
                if (explosionResistanceInput != null) {
                    DataResult<Number> explosionResistance = ops.getNumberValue(explosionResistanceInput);
                    if (explosionResistance.isError()) {
                        return explosionResistance.map($ -> null);
                    }
                    properties.explosionResistance = explosionResistance.getOrThrow().floatValue();
                    properties.wasExplosionResistanceSet = true;
                }
                T destroyTimeInput = mapLike.get("destroy_time");
                if (destroyTimeInput != null) {
                    DataResult<Number> destroyTime = ops.getNumberValue(destroyTimeInput);
                    if (destroyTime.isError()) {
                        return destroyTime.map($ -> null);
                    }
                    properties.destroyTime = destroyTime.getOrThrow().floatValue();
                }
                T requiresCorrectToolForDropsInput = mapLike.get("requires_correct_tool_for_drops");
                if (requiresCorrectToolForDropsInput != null) {
                    DataResult<Boolean> requiresCorrectToolForDrops = ops.getBooleanValue(requiresCorrectToolForDropsInput);
                    if (requiresCorrectToolForDrops.isError()) {
                        return requiresCorrectToolForDrops.map($ -> null);
                    }
                    properties.requiresCorrectToolForDrops = requiresCorrectToolForDrops.getOrThrow();
                }
                T isRandomlyTickingInput = mapLike.get("is_randomly_ticking");
                if (isRandomlyTickingInput != null) {
                    DataResult<Boolean> isRandomlyTicking = ops.getBooleanValue(isRandomlyTickingInput);
                    if (isRandomlyTicking.isError()) {
                        return isRandomlyTicking.map($ -> null);
                    }
                    properties.isRandomlyTicking = isRandomlyTicking.getOrThrow();
                }
                T frictionInput = mapLike.get("friction");
                if (frictionInput != null) {
                    DataResult<Number> friction = ops.getNumberValue(frictionInput);
                    if (friction.isError()) {
                        return friction.map($ -> null);
                    }
                    properties.friction = friction.getOrThrow().floatValue();
                }
                T speedFactorInput = mapLike.get("speed_factor");
                if (speedFactorInput != null) {
                    DataResult<Number> speedFactor = ops.getNumberValue(speedFactorInput);
                    if (speedFactor.isError()) {
                        return speedFactor.map($ -> null);
                    }
                    properties.speedFactor = speedFactor.getOrThrow().floatValue();
                }
                T jumpFactorInput = mapLike.get("jump_factor");
                if (jumpFactorInput != null) {
                    DataResult<Number> jumpFactor = ops.getNumberValue(jumpFactorInput);
                    if (jumpFactor.isError()) {
                        return jumpFactor.map($ -> null);
                    }
                    properties.jumpFactor = jumpFactor.getOrThrow().floatValue();
                }
                T canOccludeInput = mapLike.get("can_occlude");
                if (canOccludeInput != null) {
                    DataResult<Boolean> canOcclude = ops.getBooleanValue(canOccludeInput);
                    if (canOcclude.isError()) {
                        return canOcclude.map($ -> null);
                    }
                    properties.canOcclude = canOcclude.getOrThrow();
                }
                T isAirInput = mapLike.get("is_air");
                if (isAirInput != null) {
                    DataResult<Boolean> isAir = ops.getBooleanValue(isAirInput);
                    if (isAir.isError()) {
                        return isAir.map($ -> null);
                    }
                    properties.isAir = isAir.getOrThrow();
                }
                T ignitedByLavaInput = mapLike.get("ignited_by_lava");
                if (ignitedByLavaInput != null) {
                    DataResult<Boolean> ignitedByLava = ops.getBooleanValue(ignitedByLavaInput);
                    if (ignitedByLava.isError()) {
                        return ignitedByLava.map($ -> null);
                    }
                    properties.ignitedByLava = ignitedByLava.getOrThrow();
                }
                T liquidInput = mapLike.get("liquid");
                if (liquidInput != null) {
                    DataResult<Boolean> liquid = ops.getBooleanValue(liquidInput);
                    if (liquid.isError()) {
                        return liquid.map($ -> null);
                    }
                    properties.liquid = liquid.getOrThrow();
                }
                T forceSolidOffInput = mapLike.get("force_solid_off");
                if (forceSolidOffInput != null) {
                    DataResult<Boolean> forceSolidOff = ops.getBooleanValue(forceSolidOffInput);
                    if (forceSolidOff.isError()) {
                        return forceSolidOff.map($ -> null);
                    }
                    properties.forceSolidOff = forceSolidOff.getOrThrow();
                }
                T forceSolidOnInput = mapLike.get("force_solid_on");
                if (forceSolidOnInput != null) {
                    DataResult<Boolean> forceSolidOn = ops.getBooleanValue(forceSolidOnInput);
                    if (forceSolidOn.isError()) {
                        return forceSolidOn.map($ -> null);
                    }
                    properties.forceSolidOn = forceSolidOn.getOrThrow();
                }
                T spawnTerrainParticlesInput = mapLike.get("spawn_terrain_particles");
                if (spawnTerrainParticlesInput != null) {
                    DataResult<Boolean> spawnTerrainParticles = ops.getBooleanValue(spawnTerrainParticlesInput);
                    if (spawnTerrainParticles.isError()) {
                        return spawnTerrainParticles.map($ -> null);
                    }
                    properties.spawnTerrainParticles = spawnTerrainParticles.getOrThrow();
                }
                T replaceableInput = mapLike.get("replaceable");
                if (replaceableInput != null) {
                    DataResult<Boolean> replaceable = ops.getBooleanValue(replaceableInput);
                    if (replaceable.isError()) {
                        return replaceable.map($ -> null);
                    }
                    properties.replaceable = replaceable.getOrThrow();
                }
                T dynamicShapeInput = mapLike.get("dynamic_shape");
                if (dynamicShapeInput != null) {
                    DataResult<Boolean> dynamicShape = ops.getBooleanValue(dynamicShapeInput);
                    if (dynamicShape.isError()) {
                        return dynamicShape.map($ -> null);
                    }
                    properties.dynamicShape = dynamicShape.getOrThrow();
                }
                return DataResult.success(Pair.of(properties, input));
            });
        }

    };

}
