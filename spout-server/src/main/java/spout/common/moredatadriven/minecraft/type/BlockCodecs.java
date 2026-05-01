package spout.common.moredatadriven.minecraft.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperStairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import spout.common.util.mojang.codec.EnumViaIdentifierCodec;
import spout.common.util.mojang.codec.StaticFieldViaIdentifierCodec;
import spout.server.paper.impl.moredatadriven.minecraft.type.ChunkSectionLayer;
import java.util.function.BiFunction;

/**
 * Holder for codecs related to blocks.
 */
public final class BlockCodecs {

    private static final Codec<ChunkSectionLayer> CHUNK_SECTION_LAYER_CODEC = new EnumViaIdentifierCodec<>(ChunkSectionLayer.class);
    private static final Codec<MapColor> MAP_COLOR_CODEC = new StaticFieldViaIdentifierCodec<>(MapColor.class);
    private static final Codec<BlockStateFunction<MapColor>> MAP_COLOR_FUNCTION_CODEC = BlockStateFunction.codec(MAP_COLOR_CODEC);
    private static final Codec<SoundType> SOUND_TYPE_CODEC = new StaticFieldViaIdentifierCodec<>(SoundType.class);
    private static final Codec<BlockStateFunction<Integer>> LIGHT_EMISSION_CODEC = BlockStateFunction.codec(Codec.INT);
    private static final Codec<PushReaction> PUSH_REACTION_CODEC = new EnumViaIdentifierCodec<>(PushReaction.class);
    private static final Codec<NoteBlockInstrument> NOTE_BLOCK_INSTRUMENT_CODEC = new EnumViaIdentifierCodec<>(NoteBlockInstrument.class);
    private static final Codec<BlockBehaviour.OffsetType> OFFSET_TYPE_CODEC = new EnumViaIdentifierCodec<>(BlockBehaviour.OffsetType.class);

    public static final Codec<BlockBehaviour.Properties> PROPERTIES_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(BlockBehaviour.Properties input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();
            if (input.chunkSectionLayer != null) {
                builder.add("chunk_section_layer", input.chunkSectionLayer, CHUNK_SECTION_LAYER_CODEC);
            }
            builder.add("map_color", input.mapColor, MAP_COLOR_FUNCTION_CODEC);
            builder.add("has_collision", ops.createBoolean(input.hasCollision));
            builder.add("sound_type", input.soundType, SOUND_TYPE_CODEC);
            builder.add("light_emission", input.lightEmission, LIGHT_EMISSION_CODEC);
            if (input.wasExplosionResistanceSet) {
                builder.add("explosion_resistance", ops.createFloat(input.explosionResistance));
            }
            builder.add("destroy_time", ops.createFloat(input.destroyTime));
            builder.add("requires_correct_tool_for_drops", ops.createBoolean(input.requiresCorrectToolForDrops));
            builder.add("is_randomly_ticking", ops.createBoolean(input.isRandomlyTicking));
            builder.add("friction", ops.createFloat(input.friction));
            builder.add("speed_factor", ops.createFloat(input.speedFactor));
            builder.add("jump_factor", ops.createFloat(input.jumpFactor));
            if (input.id != null) {
                builder.add("id", input.id.identifier(), Identifier.CODEC);
            }
            builder.add("can_occlude", ops.createBoolean(input.canOcclude));
            builder.add("is_air", ops.createBoolean(input.isAir));
            builder.add("ignited_by_lava", ops.createBoolean(input.ignitedByLava));
            builder.add("liquid", ops.createBoolean(input.liquid));
            builder.add("force_solid_off", ops.createBoolean(input.forceSolidOff));
            builder.add("force_solid_on", ops.createBoolean(input.forceSolidOn));
            builder.add("push_reaction", input.pushReaction, PUSH_REACTION_CODEC);
            builder.add("spawn_terrain_particles", ops.createBoolean(input.spawnTerrainParticles));
            builder.add("instrument", input.instrument, NOTE_BLOCK_INSTRUMENT_CODEC);
            builder.add("replaceable", ops.createBoolean(input.replaceable));
            builder.add("is_redstone_conductor", input.isRedstoneConductor, KnownStatePredicate.CODEC);
            builder.add("is_suffocating", input.isSuffocating, KnownStatePredicate.CODEC);
            builder.add("is_view_blocking", input.isViewBlocking, KnownStatePredicate.CODEC);
            builder.add("has_post_process", input.hasPostProcess, KnownStatePredicate.CODEC);
            builder.add("emissive_rendering", input.emissiveRendering, KnownStatePredicate.CODEC);
            builder.add("dynamic_shape", ops.createBoolean(input.dynamicShape));
            builder.add("required_features", input.requiredFeatures, FeatureFlagCodecs.FEATURE_FLAG_SET_CODEC);
            if (input.offsetFunction != null) {
                builder.add("offset_function", input.offsetFunction, OFFSET_TYPE_CODEC);
            }
            return builder.build(prefix);
        }

        @Override
        public <T> DataResult<Pair<BlockBehaviour.Properties, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(mapLike -> {
                BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
                T chunkSectionLayerInput = mapLike.get("chunk_section_layer");
                if (chunkSectionLayerInput != null) {
                    DataResult<ChunkSectionLayer> chunkSectionLayer = CHUNK_SECTION_LAYER_CODEC.decode(ops, chunkSectionLayerInput).map(Pair::getFirst);
                    if (chunkSectionLayer.isError()) {
                        return chunkSectionLayer.map($ -> null);
                    }
                    properties.chunkSectionLayer = chunkSectionLayer.getOrThrow();
                }
                T mapColorInput = mapLike.get("map_color");
                if (mapColorInput != null) {
                    DataResult<BlockStateFunction<MapColor>> mapColor = MAP_COLOR_FUNCTION_CODEC.decode(ops, mapColorInput).map(Pair::getFirst);
                    if (mapColor.isError()) {
                        return mapColor.map($ -> null);
                    }
                    properties.mapColor = mapColor.getOrThrow();
                }
                T hasCollisionInput = mapLike.get("has_collision");
                if (hasCollisionInput != null) {
                    DataResult<Boolean> hasCollision = ops.getBooleanValue(hasCollisionInput);
                    if (hasCollision.isError()) {
                        return hasCollision.map($ -> null);
                    }
                    properties.hasCollision = hasCollision.getOrThrow();
                }
                T soundTypeInput = mapLike.get("sound_type");
                if (soundTypeInput != null) {
                    DataResult<SoundType> soundType = SOUND_TYPE_CODEC.decode(ops, soundTypeInput).map(Pair::getFirst);
                    if (soundType.isError()) {
                        return soundType.map($ -> null);
                    }
                    properties.soundType = soundType.getOrThrow();
                }
                T lightEmissionInput = mapLike.get("light_emission");
                if (lightEmissionInput != null) {
                    DataResult<BlockStateFunction<Integer>> lightEmission = LIGHT_EMISSION_CODEC.decode(ops, lightEmissionInput).map(Pair::getFirst);
                    if (lightEmission.isError()) {
                        return lightEmission.map($ -> null);
                    }
                    properties.lightEmission = lightEmission.getOrThrow();
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
                T idInput = mapLike.get("id");
                if (idInput != null) {
                    DataResult<Identifier> id = Identifier.CODEC.decode(ops, idInput).map(Pair::getFirst);
                    if (id.isError()) {
                        return id.map($ -> null);
                    }
                    properties.id = ResourceKey.create(BuiltInRegistries.BLOCK.key(), id.getOrThrow());
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
                T pushReactionInput = mapLike.get("push_reaction");
                if (pushReactionInput != null) {
                    DataResult<PushReaction> pushReaction = PUSH_REACTION_CODEC.decode(ops, pushReactionInput).map(Pair::getFirst);
                    if (pushReaction.isError()) {
                        return pushReaction.map($ -> null);
                    }
                    properties.pushReaction = pushReaction.getOrThrow();
                }
                T spawnTerrainParticlesInput = mapLike.get("spawn_terrain_particles");
                if (spawnTerrainParticlesInput != null) {
                    DataResult<Boolean> spawnTerrainParticles = ops.getBooleanValue(spawnTerrainParticlesInput);
                    if (spawnTerrainParticles.isError()) {
                        return spawnTerrainParticles.map($ -> null);
                    }
                    properties.spawnTerrainParticles = spawnTerrainParticles.getOrThrow();
                }
                T instrumentInput = mapLike.get("instrument");
                if (instrumentInput != null) {
                    DataResult<NoteBlockInstrument> instrument = NOTE_BLOCK_INSTRUMENT_CODEC.decode(ops, instrumentInput).map(Pair::getFirst);
                    if (instrument.isError()) {
                        return instrument.map($ -> null);
                    }
                    properties.instrument = instrument.getOrThrow();
                }
                T replaceableInput = mapLike.get("replaceable");
                if (replaceableInput != null) {
                    DataResult<Boolean> replaceable = ops.getBooleanValue(replaceableInput);
                    if (replaceable.isError()) {
                        return replaceable.map($ -> null);
                    }
                    properties.replaceable = replaceable.getOrThrow();
                }
                T isRedstoneConductorInput = mapLike.get("is_redstone_conductor");
                if (isRedstoneConductorInput != null) {
                    DataResult<KnownStatePredicate> isRedstoneConductor = KnownStatePredicate.CODEC.decode(ops, isRedstoneConductorInput).map(Pair::getFirst);
                    if (isRedstoneConductor.isError()) {
                        return isRedstoneConductor.map($ -> null);
                    }
                    properties.isRedstoneConductor = isRedstoneConductor.getOrThrow();
                }
                T isSuffocatingInput = mapLike.get("is_suffocating");
                if (isSuffocatingInput != null) {
                    DataResult<KnownStatePredicate> isSuffocating = KnownStatePredicate.CODEC.decode(ops, isSuffocatingInput).map(Pair::getFirst);
                    if (isSuffocating.isError()) {
                        return isSuffocating.map($ -> null);
                    }
                    properties.isSuffocating = isSuffocating.getOrThrow();
                }
                T isViewBlockingInput = mapLike.get("is_view_blocking");
                if (isViewBlockingInput != null) {
                    DataResult<KnownStatePredicate> isViewBlocking = KnownStatePredicate.CODEC.decode(ops, isViewBlockingInput).map(Pair::getFirst);
                    if (isViewBlocking.isError()) {
                        return isViewBlocking.map($ -> null);
                    }
                    properties.isViewBlocking = isViewBlocking.getOrThrow();
                }
                T hasPostProcessInput = mapLike.get("has_post_process");
                if (hasPostProcessInput != null) {
                    DataResult<KnownStatePredicate> hasPostProcess = KnownStatePredicate.CODEC.decode(ops, hasPostProcessInput).map(Pair::getFirst);
                    if (hasPostProcess.isError()) {
                        return hasPostProcess.map($ -> null);
                    }
                    properties.hasPostProcess = hasPostProcess.getOrThrow();
                }
                T emissiveRenderingInput = mapLike.get("emissive_rendering");
                if (emissiveRenderingInput != null) {
                    DataResult<KnownStatePredicate> emissiveRendering = KnownStatePredicate.CODEC.decode(ops, emissiveRenderingInput).map(Pair::getFirst);
                    if (emissiveRendering.isError()) {
                        return emissiveRendering.map($ -> null);
                    }
                    properties.emissiveRendering = emissiveRendering.getOrThrow();
                }
                T dynamicShapeInput = mapLike.get("dynamic_shape");
                if (dynamicShapeInput != null) {
                    DataResult<Boolean> dynamicShape = ops.getBooleanValue(dynamicShapeInput);
                    if (dynamicShape.isError()) {
                        return dynamicShape.map($ -> null);
                    }
                    properties.dynamicShape = dynamicShape.getOrThrow();
                }
                T requiredFeaturesInput = mapLike.get("required_features");
                if (requiredFeaturesInput != null) {
                    DataResult<FeatureFlagSet> requiredFeatures = FeatureFlagCodecs.FEATURE_FLAG_SET_CODEC.decode(ops, requiredFeaturesInput).map(Pair::getFirst);
                    if (requiredFeatures.isError()) {
                        return requiredFeatures.map($ -> null);
                    }
                    properties.requiredFeatures = requiredFeatures.getOrThrow();
                }
                T offsetFunctionInput = mapLike.get("offset_function");
                if (offsetFunctionInput != null) {
                    DataResult<BlockBehaviour.OffsetType> offsetFunction = OFFSET_TYPE_CODEC.decode(ops, offsetFunctionInput).map(Pair::getFirst);
                    if (offsetFunction.isError()) {
                        return offsetFunction.map($ -> null);
                    }
                    properties.offsetFunction = offsetFunction.getOrThrow();
                }
                return DataResult.success(Pair.of(properties, input));
            });
        }

    };

    private static <B extends Block, T1> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        BiFunction<T1, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            BlockBehaviour.propertiesCodec()
        ).apply(instance, factory));
    }

    private static <B extends Block, T1, T2> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        App<RecordCodecBuilder.Mu<B>, T2> t2,
        Function3<T1, T2, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            t2,
            BlockBehaviour.propertiesCodec()
        ).apply(instance, factory));
    }

    private static <B extends Block, T1, T2, T3> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        App<RecordCodecBuilder.Mu<B>, T2> t2,
        App<RecordCodecBuilder.Mu<B>, T3> t3,
        Function4<T1, T2, T3, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            t2,
            t3,
            BlockBehaviour.propertiesCodec()
        ).apply(instance, factory));
    }

    public static <B extends BrushableBlock> MapCodec<B> brushableCodec(
        Function4<Block, SoundEvent, SoundEvent, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodec(
            Identifier.CODEC.fieldOf("turns_into").forGetter(brushableBlock -> brushableBlock.turnsInto.keyInBlockRegistry),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_sound").forGetter(BrushableBlock::getBrushSound),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_completed_sound").forGetter(BrushableBlock::getBrushCompletedSound),
            (turnsIntoIdentifier, brushSound, brushCompletedSound, properties) -> {
                B block = factory.apply(Blocks.STONE /* We replace it later */, brushSound, brushCompletedSound, properties);
                block.spout$setTurnsIntoIdentifier(turnsIntoIdentifier);
                return block;
            }
        );
    }

    private static <B extends StairBlock> App<RecordCodecBuilder.Mu<B>, String> stairBaseStateApp() {
        return Codec.STRING.fieldOf("base_state").forGetter(stairBlock -> BlockStateStringConversion.blockStateToString(stairBlock.baseState));
    }

    public static <B extends StairBlock> MapCodec<B> stairCodec(
        BiFunction<BlockState, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodec(
            stairBaseStateApp(),
            (baseStateString, properties) -> {
                B block = factory.apply(Blocks.STONE.defaultBlockState() /* We replace it later */, properties);
                block.spout$setBaseStateString(baseStateString);
                return block;
            }
        );
    }

    public static <B extends WeatheringCopperStairBlock> MapCodec<B> weatheringCopperStairCodec(
        Function3<WeatheringCopper.WeatherState, BlockState, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodec(
            WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperStairBlock::getAge),
            stairBaseStateApp(),
            (weatherState, baseStateString, properties) -> {
                B block = factory.apply(weatherState, Blocks.STONE.defaultBlockState() /* We replace it later */, properties);
                block.spout$setBaseStateString(baseStateString);
                return block;
            }
        );
    }

}
