package org.fiddlemc.fiddle.api.packetmapping.block.claim;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import org.bukkit.block.data.BlockData;
import java.util.Comparator;

/**
 * A {@link Comparator} that compares {@link ClaimRequestPriority} instances based on semantic priority,
 * in other words, lower priorities are considered to be "lower" and will come first in the order.
 */
public final class ClaimRequestPriorityComparator implements Comparator<ClaimRequestPriority> {

    private final Object2DoubleMap<BlockData> baseValueMap;

    public ClaimRequestPriorityComparator(Object2DoubleMap<BlockData> baseValueMap) {
        this.baseValueMap = baseValueMap;
    }

    private double getValue(ClaimRequestPriority.ForBlockStates forBlockStates) {
        double max = 0;
        for (BlockData state : forBlockStates.blockStates) {
            max = Math.max(max, this.baseValueMap.getDouble(state));
        }
        return max * forBlockStates.factor;
    }

    private int compareBlockStates(ClaimRequestPriority.ForBlockStates forBlockStates1, ClaimRequestPriority.ForBlockStates forBlockStates2) {
        int valueCompare = Double.compare(this.getValue(forBlockStates1), this.getValue(forBlockStates2));
        if (valueCompare != 0) {
            return valueCompare;
        }
        return -Integer.compare(forBlockStates1.id, forBlockStates2.id);
    }

    @Override
    public int compare(ClaimRequestPriority o1, ClaimRequestPriority o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        return switch (o1) {
            case ClaimRequestPriority.Explicit explicit1 -> switch (o2) {
                case ClaimRequestPriority.Explicit explicit2 -> explicit1.level.compareTo(explicit2.level);
                case ClaimRequestPriority.ForBlockStates forBlockStates2 -> this.compare(forBlockStates2, explicit1);
            };
            case ClaimRequestPriority.ForBlockStates forBlockStates1 -> switch (o2) {
                case ClaimRequestPriority.Explicit explicit2 -> explicit2.level == ClaimRequestPriority.Explicit.Level.HIGHEST ? -1 : 1;
                case ClaimRequestPriority.ForBlockStates forBlockStates2 -> {
                    int blockStateCompare = this.compareBlockStates(forBlockStates1, forBlockStates2);
                    if (blockStateCompare != 0) {
                        yield blockStateCompare;
                    }
                    yield Integer.compare(forBlockStates1.id, forBlockStates2.id);
                }
            };
        };
    }

}
