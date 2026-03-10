package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.Optional;

/**
 * A utility class for transforming {@link BlockDefinition} to {@link Block}
 * and the other way around.
 */
public final class BlockDefinitionFromToBlock {

    private BlockDefinitionFromToBlock() {
        throw new UnsupportedOperationException();
    }

    public static BlockDefinition from(Block block) {
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

    public static Block to(ResourceKey<Block> id, BlockDefinition definition) {
        BlockBehaviour.Properties properties = new BlockBehaviour.Properties();
        definition.hasCollision.ifPresent(hasCollision -> properties.hasCollision = hasCollision);
        definition.explosionResistance.ifPresent(explosionResistance -> properties.explosionResistance = explosionResistance);
        definition.destroyTime.ifPresent(destroyTime -> properties.destroyTime = destroyTime);
        definition.requiresCorrectToolForDrops.ifPresent(requiresCorrectToolForDrops -> properties.requiresCorrectToolForDrops = requiresCorrectToolForDrops);
        definition.isRandomlyTicking.ifPresent(isRandomlyTicking -> properties.isRandomlyTicking = isRandomlyTicking);
        definition.friction.ifPresent(friction -> properties.friction = friction);
        definition.speedFactor.ifPresent(speedFactor -> properties.speedFactor = speedFactor);
        definition.jumpFactor.ifPresent(jumpFactor -> properties.jumpFactor = jumpFactor);
        definition.canOcclude.ifPresent(canOcclude -> properties.canOcclude = canOcclude);
        definition.isAir.ifPresent(isAir -> properties.isAir = isAir);
        definition.ignitedByLava.ifPresent(ignitedByLava -> properties.ignitedByLava = ignitedByLava);
        definition.liquid.ifPresent(liquid -> properties.liquid = liquid);
        definition.forceSolidOff.ifPresent(forceSolidOff -> properties.forceSolidOff = forceSolidOff);
        definition.forceSolidOn.ifPresent(forceSolidOn -> properties.forceSolidOn = forceSolidOn);
        definition.spawnTerrainParticles.ifPresent(spawnTerrainParticles -> properties.spawnTerrainParticles = spawnTerrainParticles);
        definition.replaceable.ifPresent(replaceable -> properties.replaceable = replaceable);
        definition.dynamicShape.ifPresent(dynamicShape -> properties.dynamicShape = dynamicShape);
        properties.setId(id);
        return new Block(properties);
    }

}
