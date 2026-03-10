package org.fiddlemc.fiddle.impl.util.mojang.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Optional;

/**
 * Provides convenience methods for dealing with {@link Codec}s.
 */
public final class CodecUtil {

    private CodecUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> Optional<T> getOptional(DynamicOps<T> ops, MapLike<T> input, String key) {
        return Optional.ofNullable(input.get(key));
    }

    public static <T, U> Optional<U> getOptional(DynamicOps<T> ops, MapLike<T> input, String key, Codec<U> codec) {
        T value = input.get(key);
        if (value == null) {
            return Optional.empty();
        }
        DataResult<Pair<U, T>> result = codec.decode(ops, value);
        if (result.isSuccess()) {
            return Optional.of(result.getOrThrow().getFirst());
        }
        return Optional.empty();
    }

    public static <T> Optional<Boolean> getOptionalBoolean(DynamicOps<T> ops, MapLike<T> input, String key) {
        T value = input.get(key);
        if (value == null) {
            return Optional.empty();
        }
        DataResult<Boolean> result = ops.getBooleanValue(value);
        if (result.isSuccess()) {
            return Optional.of(result.getOrThrow());
        }
        return Optional.empty();
    }

    public static <T> Optional<Number> getOptionalNumber(DynamicOps<T> ops, MapLike<T> input, String key) {
        T value = input.get(key);
        if (value == null) {
            return Optional.empty();
        }
        DataResult<Number> result = ops.getNumberValue(value);
        if (result.isSuccess()) {
            return Optional.of(result.getOrThrow());
        }
        return Optional.empty();
    }

    public static <T> Optional<Float> getOptionalFloat(DynamicOps<T> ops, MapLike<T> input, String key) {
        return getOptionalNumber(ops, input, key).map(Number::floatValue);
    }

    public static <T, U> void setOptional(DynamicOps<T> ops, RecordBuilder<T> builder, String key, Optional<U> value, Codec<U> codec) {
        value.ifPresent(presentValue -> builder.add(key, codec.encodeStart(ops, presentValue).getOrThrow()));
    }

    public static <T> void setOptionalBoolean(DynamicOps<T> ops, RecordBuilder<T> builder, String key, Optional<Boolean> value) {
        value.ifPresent(presentValue -> builder.add(key, ops.createBoolean(presentValue)));
    }

    public static <T> void setOptionalFloat(DynamicOps<T> ops, RecordBuilder<T> builder, String key, Optional<Float> value) {
        value.ifPresent(presentValue -> builder.add(key, ops.createFloat(presentValue)));
    }

}
