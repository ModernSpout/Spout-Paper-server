package spout.common.moredatadriven.minecraft.type;

import net.minecraft.world.item.Item;
import java.util.stream.Stream;

/**
 * A utility class that applies lazily applied item values.
 */
public final class ApplyLazyItemValues {

    private ApplyLazyItemValues() {
        throw new UnsupportedOperationException();
    }

    public static void apply(Stream<Item> items) {
        items.forEach(item -> {
            // TODO add when necessary
        });
    }

}
