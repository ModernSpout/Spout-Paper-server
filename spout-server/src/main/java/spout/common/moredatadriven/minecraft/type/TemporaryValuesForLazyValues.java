package spout.common.moredatadriven.minecraft.type;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A holder for temporary {@link Block} and {@link Item} values,
 * that are later used to instantiate lazy values.
 */
public final class TemporaryValuesForLazyValues {

    private TemporaryValuesForLazyValues() {
        throw new UnsupportedOperationException();
    }

    private final static Map<Block, Identifier> blockIdentifier = new HashMap<>(1);
    private final static Map<Block, String> blockString = new HashMap<>(1);

    public static void setBlockIdentifier(Block block, Identifier identifier) {
        blockIdentifier.put(block, identifier);
    }

    public static @Nullable Identifier pollBlockIdentifier(Block block) {
        return blockIdentifier.remove(block);
    }

    public static void setBlockString(Block block, String string) {
        blockString.put(block, string);
    }

    public static @Nullable String pollBlockString(Block block) {
        return blockString.remove(block);
    }

}
