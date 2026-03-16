package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BlockTypes;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;

/**
 * Analogous to {@link BlockTypes}, but for {@link Item}s.
 */
public final class ItemTypes {

    private ItemTypes() {
        throw new UnsupportedOperationException();
    }

    public static final MapCodec<Item> CODEC = BuiltInRegistries.FIDDLE_ITEM_TYPE.byNameCodec().dispatchMap(item -> WrappedItemCodecImpl.wrap(item.codec()), WrappedItemCodec::getCodec);

    public static WrappedItemCodec<? extends Item> bootstrap(Registry<WrappedItemCodec<? extends Item>> registry) {
        class Registry {
            static WrappedItemCodec<?> register(net.minecraft.core.Registry<WrappedItemCodec<? extends Item>> registry, String name, MapCodec<? extends Item> value) {
                return net.minecraft.core.Registry.register(registry, name, WrappedItemCodecImpl.wrap(value));
            }
        }
        Registry.register(registry, "item", ItemCodecs.ITEM_CODEC);
        Registry.register(registry, "block", ItemCodecs.BLOCK_ITEM_CODEC);
        return Registry.register(registry, "egg", ItemCodecs.EGG_ITEM_CODEC);
        // TODO others
    }

}
