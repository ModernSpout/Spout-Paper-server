package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.fiddlemc.fiddle.impl.util.mojang.codec.CodecUtil;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Holder for {@link Item} codecs.
 */
public final class ItemCodecs {

    private ItemCodecs() {
        throw new UnsupportedOperationException();
    }

    private static Item.Properties constructItemProperties(DataComponentMap components, FeatureFlagSet requiredFeatures) {
        Item.Properties properties = new Item.Properties();
        properties.components.addAll(components);
        properties.requiredFeatures = requiredFeatures;
        return properties;
    }

    private static Item.Properties reconstructItemProperties(Item item) {
        return constructItemProperties(item.components(), item.requiredFeatures());
    }

    private static final Codec<Item.Properties> ITEM_PROPERTIES_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY).forGetter(properties -> properties.components.build()),
        CodecUtil.optionalFieldOf(FeatureFlagCodecs.FEATURE_FLAG_SET_CODEC, "required_features", FeatureFlagSet::of).forGetter(properties -> properties.requiredFeatures)
    ).apply(instance, ItemCodecs::constructItemProperties));

    private static <I extends Item> RecordCodecBuilder<I, Item.Properties> propertiesCodec() {
        return CodecUtil.optionalFieldOf(ITEM_PROPERTIES_CODEC, "properties", Item.Properties::new).forGetter(ItemCodecs::reconstructItemProperties);
    }

    /**
     * Based on {@link BlockBehaviour#simpleCodec}.
     */
    private static <I extends Item> MapCodec<I> simpleCodec(
        Function<Item.Properties, I> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec()
        ).apply(instance, factory));
    }

    private static <I extends Item, T1> MapCodec<I> simpleCodec(
        App<RecordCodecBuilder.Mu<I>, T1> t1,
        BiFunction<T1, Item.Properties, I> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            propertiesCodec()
        ).apply(instance, factory));
    }

    /**
     * Based on {@link Block#CODEC}.
     */
    public static final MapCodec<Item> ITEM_CODEC = simpleCodec(Item::new);

    /**
     * Based on {@link Block#CODEC}.
     */
    public static final MapCodec<BlockItem> BLOCK_ITEM_CODEC = simpleCodec(
        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockItem::getBlock),
        (block, properties) -> new BlockItem(block, properties.useBlockDescriptionPrefix())
    );

    /**
     * Based on {@link Block#CODEC}.
     */
    public static final MapCodec<EggItem> EGG_ITEM_CODEC = simpleCodec(EggItem::new);

}
