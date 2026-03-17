package org.fiddlemc.fiddle.impl.util.mojang.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.Identifier;
import java.util.Locale;

/**
 * A {@link Codec} for enums,
 * where values are encoded as a {@link Identifier#DEFAULT_NAMESPACE} identifier.
 */
public class EnumViaIdentifierCodec<A extends Enum<A>> implements Codec<A> {

    private final Class<A> typeClass;

    public EnumViaIdentifierCodec(Class<A> typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return Identifier.CODEC.encode(Identifier.parse(input.name().toLowerCase(Locale.ROOT)), ops, prefix);
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return Identifier.CODEC.decode(ops, input).flatMap(key -> {
            if (key.getFirst().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
                try {
                    return DataResult.success(Pair.of(Enum.valueOf(this.typeClass, key.getFirst().getPath().toUpperCase(Locale.ROOT)), input));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return DataResult.error(() -> "Unknown " + typeClass.getSimpleName() + ": " + key);
        });
    }

}
