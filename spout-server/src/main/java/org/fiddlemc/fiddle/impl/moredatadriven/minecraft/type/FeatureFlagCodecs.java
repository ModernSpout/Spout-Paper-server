package org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import java.util.Arrays;

/**
 * Holder for codecs related to {@link FeatureFlag}s.
 */
public final class FeatureFlagCodecs {

    private FeatureFlagCodecs() {
        throw new UnsupportedOperationException();
    }

    public static final Codec<FeatureFlag> FEATURE_FLAG_CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        FeatureFlag featureFlag = FeatureFlags.REGISTRY.names.get(identifier);
        if (featureFlag != null) {
            return DataResult.success(featureFlag);
        }
        return DataResult.error(() -> "No such feature flag: " + identifier);
    }, featureFlag -> FeatureFlags.REGISTRY.names.entrySet().stream().filter(entry -> entry.getValue().equals(featureFlag)).findAny().get().getKey());

    public static final Codec<FeatureFlagSet> FEATURE_FLAG_SET_CODEC = FEATURE_FLAG_CODEC.listOf().xmap(
        featureFlags -> {
            FeatureFlag[] array = featureFlags.toArray(FeatureFlag[]::new);
            return array.length == 0 ? FeatureFlagSet.of() : array.length == 1 ? FeatureFlagSet.of(array[0]) : FeatureFlagSet.of(array[0], Arrays.copyOfRange(array, 1, array.length));
        },
        featureFlagSet -> FeatureFlags.REGISTRY.names.values().stream().filter(featureFlagSet::contains).toList()
    );

}
