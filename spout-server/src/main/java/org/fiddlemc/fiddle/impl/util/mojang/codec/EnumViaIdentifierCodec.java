package org.fiddlemc.fiddle.impl.util.mojang.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import java.util.Locale;

/**
 * A {@link Codec} for enums,
 * where values are encoded as a {@link Identifier#DEFAULT_NAMESPACE} identifier.
 */
public class EnumViaIdentifierCodec<A extends Enum<A>> implements Codec<A> {

    private final Class<A> typeClass;
    private final @Nullable String defaultNamespace;

    public EnumViaIdentifierCodec(Class<A> typeClass, @Nullable String defaultNamespace) {
        this.typeClass = typeClass;
        this.defaultNamespace = defaultNamespace;
    }

    public EnumViaIdentifierCodec(Class<A> typeClass) {
        this(typeClass, null);
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        String path = input.name().toLowerCase(Locale.ROOT);
        Identifier identifier = this.defaultNamespace == null ? Identifier.parse(path) : Identifier.fromNamespaceAndPath(this.defaultNamespace, path);
        return Identifier.CODEC.encode(identifier, ops, prefix);
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<Identifier, T>> identifierResult;
        if (this.defaultNamespace == null) {
            identifierResult = Identifier.CODEC.decode(ops, input);
        } else {
            identifierResult = ops.getStringValue(input).map(string -> {
                if (string.indexOf(Identifier.NAMESPACE_SEPARATOR) == -1) {
                    string = this.defaultNamespace + Identifier.NAMESPACE_SEPARATOR + string;
                }
                return Pair.of(Identifier.parse(string), input);
            });
        }
        return identifierResult.flatMap(key -> {
            if (key.getFirst().getNamespace().equals(this.defaultNamespace != null ? this.defaultNamespace : Identifier.DEFAULT_NAMESPACE)) {
                try {
                    return DataResult.success(Pair.of(Enum.valueOf(this.typeClass, key.getFirst().getPath().toUpperCase(Locale.ROOT)), input));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return DataResult.error(() -> "Unknown " + typeClass.getSimpleName() + ": " + key);
        });
    }

}
