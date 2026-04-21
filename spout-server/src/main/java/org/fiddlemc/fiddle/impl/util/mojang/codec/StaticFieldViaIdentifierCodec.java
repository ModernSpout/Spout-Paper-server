package org.fiddlemc.fiddle.impl.util.mojang.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A {@link Codec} for a type, which is like an enum,
 * but has its possible values stored in public static final fields in its class.
 *
 * <p>
 * Values are encoded as a {@link Identifier#DEFAULT_NAMESPACE} identifier.
 * </p>
 */
public class StaticFieldViaIdentifierCodec<A> implements Codec<A> {

    private final Class<A> typeClass;

    private @Nullable Map<Identifier, A> fromKey;
    private @Nullable Map<A, Identifier> toKey;

    public StaticFieldViaIdentifierCodec(Class<A> typeClass) {
        this.typeClass = typeClass;
    }

    private void initialize() {
        if (this.fromKey == null) {
            List<Field> fields = Arrays.stream(this.typeClass.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                .filter(field -> this.typeClass.isAssignableFrom(field.getType()))
                .toList();
            this.fromKey = new HashMap<>(fields.size());
            this.toKey = new HashMap<>(fields.size());
            for (Field field : fields) {
                Identifier key = Identifier.parse(field.getName().toLowerCase(Locale.ROOT));
                A value;
                try {
                    field.trySetAccessible();
                    value = (A) field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                this.fromKey.put(key, value);
                this.toKey.put(value, key);
            }
        }
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        this.initialize();
        return Identifier.CODEC.encode(this.toKey.get(input), ops, prefix);
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        this.initialize();
        return Identifier.CODEC.decode(ops, input).flatMap(key -> {
            A value = this.fromKey.get(key.getFirst());
            return value != null ? DataResult.success(Pair.of(value, input)) : DataResult.error(() -> "Unknown map color: " + key);
        });
    }

}
