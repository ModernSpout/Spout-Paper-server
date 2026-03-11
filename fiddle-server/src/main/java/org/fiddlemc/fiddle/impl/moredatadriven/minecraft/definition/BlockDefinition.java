package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.definition;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.fiddlemc.fiddle.impl.util.mojang.codec.CodecUtil;
import java.util.Optional;

/**
 * A definition of a {@link Block} resource, which can be serialized.
 */
public final class BlockDefinition implements Definition<Block> {

    public static final Codec<BlockDefinition> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<Pair<BlockDefinition, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).map(mapLike -> {
                BlockDefinition definition = new BlockDefinition();
                definition.hasCollision = CodecUtil.getOptionalBoolean(ops, mapLike, "has_collision");
                definition.explosionResistance = CodecUtil.getOptionalFloat(ops, mapLike, "explosion_resistance");
                definition.destroyTime = CodecUtil.getOptionalFloat(ops, mapLike, "destroy_time");
                definition.requiresCorrectToolForDrops = CodecUtil.getOptionalBoolean(ops, mapLike, "requires_correct_tool_for_drops");
                definition.isRandomlyTicking = CodecUtil.getOptionalBoolean(ops, mapLike, "is_randomly_ticking");
                definition.friction = CodecUtil.getOptionalFloat(ops, mapLike, "friction");
                definition.speedFactor = CodecUtil.getOptionalFloat(ops, mapLike, "speed_factor");
                definition.jumpFactor = CodecUtil.getOptionalFloat(ops, mapLike, "jump_factor");
                definition.canOcclude = CodecUtil.getOptionalBoolean(ops, mapLike, "can_occlude");
                definition.isAir = CodecUtil.getOptionalBoolean(ops, mapLike, "is_air");
                definition.ignitedByLava = CodecUtil.getOptionalBoolean(ops, mapLike, "ignited_by_lava");
                definition.liquid = CodecUtil.getOptionalBoolean(ops, mapLike, "liquid");
                definition.forceSolidOff = CodecUtil.getOptionalBoolean(ops, mapLike, "force_solid_off");
                definition.forceSolidOn = CodecUtil.getOptionalBoolean(ops, mapLike, "force_solid_on");
                definition.spawnTerrainParticles = CodecUtil.getOptionalBoolean(ops, mapLike, "spawn_terrain_particles");
                definition.replaceable = CodecUtil.getOptionalBoolean(ops, mapLike, "replaceable");
                definition.dynamicShape = CodecUtil.getOptionalBoolean(ops, mapLike, "dynamic_shape");
                return Pair.of(definition, input);
            });
        }

        @Override
        public <T> DataResult<T> encode(BlockDefinition input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();
            CodecUtil.setOptionalBoolean(ops, builder, "has_collision", input.hasCollision);
            CodecUtil.setOptionalFloat(ops, builder, "explosion_resistance", input.explosionResistance);
            CodecUtil.setOptionalFloat(ops, builder, "destroy_time", input.destroyTime);
            CodecUtil.setOptionalBoolean(ops, builder, "requires_correct_tool_for_drops", input.requiresCorrectToolForDrops);
            CodecUtil.setOptionalBoolean(ops, builder, "is_randomly_ticking", input.isRandomlyTicking);
            CodecUtil.setOptionalFloat(ops, builder, "friction", input.friction);
            CodecUtil.setOptionalFloat(ops, builder, "speed_factor", input.speedFactor);
            CodecUtil.setOptionalFloat(ops, builder, "jump_factor", input.jumpFactor);
            CodecUtil.setOptionalBoolean(ops, builder, "can_occlude", input.canOcclude);
            CodecUtil.setOptionalBoolean(ops, builder, "is_air", input.isAir);
            CodecUtil.setOptionalBoolean(ops, builder, "ignited_by_lava", input.ignitedByLava);
            CodecUtil.setOptionalBoolean(ops, builder, "liquid", input.liquid);
            CodecUtil.setOptionalBoolean(ops, builder, "force_solid_off", input.forceSolidOff);
            CodecUtil.setOptionalBoolean(ops, builder, "force_solid_on", input.forceSolidOn);
            CodecUtil.setOptionalBoolean(ops, builder, "spawn_terrain_particles", input.spawnTerrainParticles);
            CodecUtil.setOptionalBoolean(ops, builder, "replaceable", input.replaceable);
            CodecUtil.setOptionalBoolean(ops, builder, "dynamic_shape", input.dynamicShape);
            return builder.build(prefix);
        }

    };

    // Missing: mapColor

    public Optional<Boolean> hasCollision = Optional.empty();

    // Missing: soundType, lightEmission

    public Optional<Float> explosionResistance = Optional.empty();
    public Optional<Float> destroyTime = Optional.empty();
    public Optional<Boolean> requiresCorrectToolForDrops = Optional.empty();
    public Optional<Boolean> isRandomlyTicking = Optional.empty();
    public Optional<Float> friction = Optional.empty();
    public Optional<Float> speedFactor = Optional.empty();
    public Optional<Float> jumpFactor = Optional.empty();

    // Not included on purpose: id, drops (these are not defined through definitions, but through other means)
    // Missing: descriptionId

    public Optional<Boolean> canOcclude = Optional.empty(); // Only for internal use
    public Optional<Boolean> isAir = Optional.empty();
    public Optional<Boolean> ignitedByLava = Optional.empty();
    public Optional<Boolean> liquid = Optional.empty();
    public Optional<Boolean> forceSolidOff = Optional.empty();
    public Optional<Boolean> forceSolidOn = Optional.empty();

    // Missing: pushReaction

    public Optional<Boolean> spawnTerrainParticles = Optional.empty();

    // Missing: instrument

    public Optional<Boolean> replaceable = Optional.empty();

    // Missing: isValidSpawn, isRedstoneConductor, isSuffocating, isViewBlocking, hasPostProcess, emissiveRendering

    public Optional<Boolean> dynamicShape = Optional.empty();

    // Missing: requiredFeatures, offsetFunction

    public BlockDefinition() {
    }

    public BlockDefinition(
        Optional<Boolean> hasCollision,
        Optional<Float> explosionResistance,
        Optional<Float> destroyTime,
        Optional<Boolean> requiresCorrectToolForDrops,
        Optional<Boolean> isRandomlyTicking,
        Optional<Float> friction,
        Optional<Float> speedFactor,
        Optional<Float> jumpFactor,
        Optional<Boolean> canOcclude,
        Optional<Boolean> isAir,
        Optional<Boolean> ignitedByLava,
        Optional<Boolean> liquid,
        Optional<Boolean> forceSolidOff,
        Optional<Boolean> forceSolidOn,
        Optional<Boolean> spawnTerrainParticles,
        Optional<Boolean> replaceable,
        Optional<Boolean> dynamicShape
    ) {
        this.hasCollision = hasCollision;
        this.explosionResistance = explosionResistance;
        this.destroyTime = destroyTime;
        this.requiresCorrectToolForDrops = requiresCorrectToolForDrops;
        this.isRandomlyTicking = isRandomlyTicking;
        this.friction = friction;
        this.speedFactor = speedFactor;
        this.jumpFactor = jumpFactor;
        this.canOcclude = canOcclude;
        this.isAir = isAir;
        this.ignitedByLava = ignitedByLava;
        this.liquid = liquid;
        this.forceSolidOff = forceSolidOff;
        this.forceSolidOn = forceSolidOn;
        this.spawnTerrainParticles = spawnTerrainParticles;
        this.replaceable = replaceable;
        this.dynamicShape = dynamicShape;
    }

    @Override
    public Block toResource(ResourceKey<Block> id) {
        BlockBehaviour.Properties properties = new BlockBehaviour.Properties();
        this.hasCollision.ifPresent(hasCollision -> properties.hasCollision = hasCollision);
        this.explosionResistance.ifPresent(explosionResistance -> properties.explosionResistance = explosionResistance);
        this.destroyTime.ifPresent(destroyTime -> properties.destroyTime = destroyTime);
        this.requiresCorrectToolForDrops.ifPresent(requiresCorrectToolForDrops -> properties.requiresCorrectToolForDrops = requiresCorrectToolForDrops);
        this.isRandomlyTicking.ifPresent(isRandomlyTicking -> properties.isRandomlyTicking = isRandomlyTicking);
        this.friction.ifPresent(friction -> properties.friction = friction);
        this.speedFactor.ifPresent(speedFactor -> properties.speedFactor = speedFactor);
        this.jumpFactor.ifPresent(jumpFactor -> properties.jumpFactor = jumpFactor);
        this.canOcclude.ifPresent(canOcclude -> properties.canOcclude = canOcclude);
        this.isAir.ifPresent(isAir -> properties.isAir = isAir);
        this.ignitedByLava.ifPresent(ignitedByLava -> properties.ignitedByLava = ignitedByLava);
        this.liquid.ifPresent(liquid -> properties.liquid = liquid);
        this.forceSolidOff.ifPresent(forceSolidOff -> properties.forceSolidOff = forceSolidOff);
        this.forceSolidOn.ifPresent(forceSolidOn -> properties.forceSolidOn = forceSolidOn);
        this.spawnTerrainParticles.ifPresent(spawnTerrainParticles -> properties.spawnTerrainParticles = spawnTerrainParticles);
        this.replaceable.ifPresent(replaceable -> properties.replaceable = replaceable);
        this.dynamicShape.ifPresent(dynamicShape -> properties.dynamicShape = dynamicShape);
        properties.setId(id);
        return new Block(properties);
    }

    /**
     * @return A definition for the given {@link Block}.
     */
    public static BlockDefinition fromResource(Block block) {
        BlockBehaviour.Properties properties = block.properties();
        return new BlockDefinition(
            Optional.of(properties.hasCollision),
            Optional.of(properties.explosionResistance),
            Optional.of(properties.destroyTime),
            Optional.of(properties.requiresCorrectToolForDrops),
            Optional.of(properties.isRandomlyTicking),
            Optional.of(properties.friction),
            Optional.of(properties.speedFactor),
            Optional.of(properties.jumpFactor),
            Optional.of(properties.canOcclude),
            Optional.of(properties.isAir),
            Optional.of(properties.ignitedByLava),
            Optional.of(properties.liquid),
            Optional.of(properties.forceSolidOff),
            Optional.of(properties.forceSolidOn),
            Optional.of(properties.spawnTerrainParticles),
            Optional.of(properties.replaceable),
            Optional.of(properties.dynamicShape)
        );
    }

}
