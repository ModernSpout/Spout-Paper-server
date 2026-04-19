package org.fiddlemc.fiddle.impl.moredatadriven.paper.registry;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import org.bukkit.FeatureFlag;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.ItemRegistryEntry;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.nms.ItemRegistryEntryBuilderNMS;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The implementation of {@link ItemRegistryEntry}.
 */
public abstract class ItemRegistryEntryImpl implements ItemRegistryEntry, SettableKeyAwareRegistryEntryNMS {

    protected @Nullable Identifier key;

    public ItemRegistryEntryImpl(
        Conversions conversions,
        @Nullable Item internal
    ) {
        if (internal != null) {
            this.key = internal.keyInItemRegistry;
        }
    }

    @Override
    public Identifier getKeyNMS() {
        return this.key;
    }

    public static final class BuilderImpl extends ItemRegistryEntryImpl implements ItemRegistryEntryBuilderNMS, PaperRegistryBuilder<Item, ItemType> {

        private @Nullable Function<Item.Properties, Item> nmsFactory;
        private Item.@Nullable Properties nmsProperties;

        public BuilderImpl(Conversions conversions, @Nullable Item internal) {
            super(conversions, internal);
        }

        @Override
        public Builder inheritsFromItem() {
            return this.factoryNMS(Item::new);
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl inheritsFromBlock() {
            return this.factoryForBlockNMS();
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl inheritsFromBlock(NamespacedKey blockKey) {
            return this.factoryForBlockNMS(CraftNamespacedKey.toMinecraft(blockKey));
        }

        @Override
        public Builder inheritsFromEgg() {
            return this.factoryNMS(EggItem::new);
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl usingConvertsTo(ItemType usingConvertsTo) {
            return this.propertiesNMS(properties -> properties.usingConvertsTo(CraftItemType.bukkitToMinecraftNew(usingConvertsTo)));
        }

        @Override
        public Builder useCooldown(float useCooldown) {
            return this.propertiesNMS(properties -> properties.useCooldown(useCooldown));
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl stacksTo(int maxStackSize) {
            return this.propertiesNMS(properties -> properties.stacksTo(maxStackSize));
        }

        @Override
        public Builder durability(int maxDamage) {
            return this.propertiesNMS(properties -> properties.durability(maxDamage));
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl craftRemainder(ItemType craftingRemainingItem) {
            return this.propertiesNMS(properties -> properties.craftRemainder(CraftItemType.bukkitToMinecraftNew(craftingRemainingItem)));
        }

        @Override
        public Builder rarity(ItemRarity rarity) {
            return this.propertiesNMS(properties -> properties.rarity(Rarity.valueOf(rarity.name())));
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl fireResistant() {
            return this.propertiesNMS(properties -> properties.fireResistant());
        }

        @Override
        public Builder jukeboxPlayable(JukeboxSong song) {
            return this.propertiesNMS(properties -> properties.jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, CraftNamespacedKey.toMinecraft(song.getKey()))));
        }

        @Override
        public Builder enchantable(int enchantmentValue) {
            return this.propertiesNMS(properties -> properties.enchantable(enchantmentValue));
        }

        @Override
        public Builder repairable(ItemType repairItem) {
            return this.propertiesNMS(properties -> properties.repairable(CraftItemType.bukkitToMinecraftNew(repairItem)));
        }

        @Override
        public Builder equippable(EquipmentSlot slot) {
            return this.propertiesNMS(properties -> properties.equippable(CraftEquipmentSlot.getNMS(slot)));
        }

        @Override
        public Builder equippableUnswappable(EquipmentSlot slot) {
            return this.propertiesNMS(properties -> properties.equippableUnswappable(CraftEquipmentSlot.getNMS(slot)));
        }

        @Override
        public Builder spawnEgg(EntityType entityType) {
            return this.propertiesNMS(properties -> properties.spawnEgg(CraftEntityType.bukkitToMinecraft(entityType)));
        }

        @Override
        public Builder trimMaterial(TrimMaterial trimMaterial) {
            return this.propertiesNMS(properties -> properties.trimMaterial(ResourceKey.create(Registries.TRIM_MATERIAL, CraftNamespacedKey.toMinecraft(trimMaterial.getKey()))));
        }

        @Override
        public Builder requiredFeatures(FeatureFlag... requiredFeatures) {
            return this.propertiesNMS(properties -> properties.requiredFeatures(Arrays.stream(requiredFeatures).map(flag -> FeatureFlags.REGISTRY.names.get(CraftNamespacedKey.toMinecraft(flag.getKey()))).toArray(net.minecraft.world.flag.FeatureFlag[]::new)));
        }

        @Override
        public Builder useBlockDescriptionPrefix() {
            return this.propertiesNMS(Item.Properties::useBlockDescriptionPrefix);
        }

        @Override
        public Builder useItemDescriptionPrefix() {
            return this.propertiesNMS(Item.Properties::useItemDescriptionPrefix);
        }

        @Override
        public void setKeyNMS(Identifier key) {
            this.key = key;
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl factoryNMS(Function<Item.Properties, Item> factory) {
            this.nmsFactory = factory;
            return this;
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl factoryForBlockNMS() {
            return this.factoryForBlockNMS(BlockItem::new);
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl factoryForBlockNMS(Identifier blockIdentifier) {
            return this.factoryForBlockNMS(blockIdentifier, BlockItem::new);
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl factoryForBlockNMS(BiFunction<Block, Item.Properties, BlockItem> factory) {
           return this.factoryForBlockNMS(this.key, factory);
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl factoryForBlockNMS(Identifier blockIdentifier, BiFunction<Block, Item.Properties, BlockItem> factory) {
            this.factoryNMS(properties -> factory.apply(BlockRegistry.get().getValue(blockIdentifier), properties));
            return this.propertiesNMS(Item.Properties::useBlockDescriptionPrefix);
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl propertiesNMS(Item.Properties properties) {
            this.nmsProperties = properties;
            return this;
        }

        @Override
        public ItemRegistryEntryImpl.BuilderImpl propertiesNMS(Consumer<Item.Properties> properties) {
            if (this.nmsProperties == null) {
                this.nmsProperties = new Item.Properties();
            }
            properties.accept(this.nmsProperties);
            return this;
        }

        @Override
        public Item build() {
            Function<Item.Properties, Item> factory = this.nmsFactory != null ? this.nmsFactory : Item::new;
            Item.Properties properties = this.nmsProperties != null ? this.nmsProperties : new Item.Properties();
            properties.setId(ResourceKey.create(Registries.ITEM, this.key));
            return factory.apply(properties);
        }

    }

}
