package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CactusFlowerBlock;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import org.bukkit.FeatureFlag;
import org.bukkit.Instrument;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftSoundGroup;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.BlockRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.nms.BlockRegistryEntryBuilderNMS;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * The implementation of {@link BlockRegistryEntry}.
 */
public abstract class BlockRegistryEntryImpl implements BlockRegistryEntry, SettableKeyAwareRegistryEntryNMS {

    protected @Nullable Identifier key;

    public BlockRegistryEntryImpl(
        Conversions conversions,
        @Nullable Block internal
    ) {
        if (internal != null) {
            this.key = internal.keyInBlockRegistry;
        }
    }

    @Override
    public Identifier getKeyNMS() {
        return this.key;
    }

    public static final class BuilderImpl extends BlockRegistryEntryImpl implements BlockRegistryEntryBuilderNMS, PaperRegistryBuilder<Block, BlockType> {

        private @Nullable Function<Block.Properties, Block> nmsFactory;
        private BlockBehaviour.@Nullable Properties nmsProperties;

        public BuilderImpl(Conversions conversions, @Nullable Block internal) {
            super(conversions, internal);
        }

        @Override
        public Builder inheritsFromBlock() {
            return this.factoryNMS(Block::new);
        }

        @Override
        public Builder inheritsFromAnvil() {
            return this.factoryNMS(AnvilBlock::new);
        }

        @Override
        public Builder inheritsFromBarrel() {
            return this.factoryNMS(BarrelBlock::new);
        }

        @Override
        public Builder inheritsFromCactus() {
            return this.factoryNMS(properties -> new CactusBlock(properties) {
            });
        }

        @Override
        public Builder inheritsFromCactusFlower() {
            return this.factoryNMS(CactusFlowerBlock::new);
        }

        @Override
        public Builder inheritsFromCake() {
            return this.factoryNMS(properties -> new CakeBlock(properties) {
            });
        }

        @Override
        public Builder inheritsFromCampfire(boolean spawnParticles, int fireDamage) {
            return this.factoryNMS(properties -> new CampfireBlock(spawnParticles, fireDamage, properties));
        }

        @Override
        public Builder inheritsFromCandle() {
            return this.factoryNMS(CandleBlock::new);
        }

        @Override
        public Builder inheritsFromCandleCake(BlockType candleBlock) {
            return this.factoryNMS(properties -> new CandleCakeBlock(CraftBlockType.bukkitToMinecraftNew(candleBlock), properties) {
            });
        }

        @Override
        public Builder inheritsFromCarpet() {
            return this.factoryNMS(CarpetBlock::new);
        }

        @Override
        public Builder inheritsFromCarvedPumpkin() {
            return this.factoryNMS(properties -> new CarvedPumpkinBlock(properties) {
            });
        }

        @Override
        public Builder inheritsFromChain() {
            return this.factoryNMS(ChainBlock::new);
        }

        @Override
        public Builder inheritsFromFire() {
            return this.factoryNMS(FireBlock::new);
        }

        @Override
        public Builder inheritsFromSlab() {
            return this.factoryNMS(SlabBlock::new);
        }

        @Override
        public Builder inheritsFromSoulFire() {
            return this.factoryNMS(SoulFireBlock::new);
        }

        @Override
        public Builder inheritsFromStairs(BlockData baseState) {
            return this.factoryNMS(properties -> new StairBlock(((CraftBlockData) baseState).getState(), properties) {
            });
        }

        @Override
        public Builder mapColor(BlockType referenceBlockType) {
            return this.propertiesNMS(properties -> properties.mapColor(CraftBlockType.bukkitToMinecraftNew(referenceBlockType).defaultMapColor()));
        }

        @Override
        public Builder noCollision() {
            return this.propertiesNMS(BlockBehaviour.Properties::noCollision);
        }

        @Override
        public Builder noOcclusion() {
            return this.propertiesNMS(BlockBehaviour.Properties::noCollision);
        }

        @Override
        public Builder friction(float friction) {
            return this.propertiesNMS(properties -> properties.friction(friction));
        }

        @Override
        public Builder speedFactor(float speedFactor) {
            return this.propertiesNMS(properties -> properties.speedFactor(speedFactor));
        }

        @Override
        public Builder jumpFactor(float jumpFactor) {
            return this.propertiesNMS(properties -> properties.jumpFactor(jumpFactor));
        }

        @Override
        public Builder sound(SoundGroup sound) {
            return this.propertiesNMS(properties -> properties.sound(((CraftSoundGroup) sound).getHandle()));
        }

        @Override
        public Builder lightLevel(ToIntFunction<BlockData> lightEmission) {
            return this.propertiesNMS(properties -> properties.lightLevel(state -> lightEmission.applyAsInt(CraftBlockData.fromData(state))));
        }

        @Override
        public Builder strength(float destroyTime, float explosionResistance) {
            return this.destroyTime(destroyTime).explosionResistance(explosionResistance);
        }

        @Override
        public Builder instabreak() {
            return this.propertiesNMS(BlockBehaviour.Properties::instabreak);
        }

        @Override
        public Builder strength(float strength) {
            return this.strength(strength, strength);
        }

        @Override
        public Builder randomTicks() {
            return this.propertiesNMS(BlockBehaviour.Properties::randomTicks);
        }

        @Override
        public Builder dynamicShape() {
            return this.propertiesNMS(BlockBehaviour.Properties::dynamicShape);
        }

        @Override
        public Builder ignitedByLava() {
            return this.propertiesNMS(BlockBehaviour.Properties::ignitedByLava);
        }

        @Override
        public Builder liquid() {
            return this.propertiesNMS(BlockBehaviour.Properties::liquid);
        }

        @Override
        public Builder pushReaction(PistonMoveReaction pushReaction) {
            return this.propertiesNMS(properties -> properties.pushReaction(PushReaction.values()[pushReaction.ordinal()]));
        }

        @Override
        public Builder air() {
            return this.propertiesNMS(BlockBehaviour.Properties::air);
        }

        @Override
        public Builder requiresCorrectToolForDrops() {
            return this.propertiesNMS(BlockBehaviour.Properties::requiresCorrectToolForDrops);
        }

        @Override
        public Builder destroyTime(float destroyTime) {
            return this.propertiesNMS(properties -> properties.destroyTime(destroyTime));
        }

        @Override
        public Builder explosionResistance(float explosionResistance) {
            return this.propertiesNMS(properties -> properties.explosionResistance(explosionResistance));
        }

        @Override
        public Builder offsetType(OffsetType offsetType) {
            return this.propertiesNMS(properties -> properties.offsetType(BlockBehaviour.OffsetType.valueOf(offsetType.name())));
        }

        @Override
        public Builder noTerrainParticles() {
            return this.propertiesNMS(BlockBehaviour.Properties::noTerrainParticles);
        }

        @Override
        public Builder requiredFeatures(FeatureFlag... requiredFeatures) {
            return this.propertiesNMS(properties -> properties.requiredFeatures(Arrays.stream(requiredFeatures).map(flag -> FeatureFlags.REGISTRY.names.get(CraftNamespacedKey.toMinecraft(flag.getKey()))).toArray(net.minecraft.world.flag.FeatureFlag[]::new)));
        }

        @Override
        public Builder instrument(Instrument instrument) {
            return this.propertiesNMS(properties -> properties.instrument(CraftBlockData.toNMS(instrument, NoteBlockInstrument.class)));
        }

        @Override
        public Builder replaceable() {
            return this.propertiesNMS(BlockBehaviour.Properties::replaceable);
        }

        @Override
        public void setKeyNMS(Identifier key) {
            this.key = key;
        }

        @Override
        public BuilderImpl factoryNMS(Function<BlockBehaviour.Properties, Block> factory) {
            this.nmsFactory = factory;
            return this;
        }

        @Override
        public BuilderImpl propertiesNMS(BlockBehaviour.Properties properties) {
            this.nmsProperties = properties;
            return this;
        }

        @Override
        public BuilderImpl propertiesNMS(Consumer<BlockBehaviour.Properties> properties) {
            if (this.nmsProperties == null) {
                this.nmsProperties = BlockBehaviour.Properties.of();
            }
            properties.accept(this.nmsProperties);
            return this;
        }

        @Override
        public Block build() {
            Function<BlockBehaviour.Properties, Block> factory = this.nmsFactory != null ? this.nmsFactory : Block::new;
            BlockBehaviour.Properties properties = this.nmsProperties != null ? this.nmsProperties : BlockBehaviour.Properties.of();
            properties.setId(ResourceKey.create(Registries.BLOCK, this.key));
            return factory.apply(properties);
        }

    }

}
