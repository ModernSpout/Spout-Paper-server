package org.fiddlemc.fiddle.impl.util.java.unsafe;

import org.fiddlemc.fiddle.impl.util.java.reflect.ReflectionUtil;
import org.jspecify.annotations.Nullable;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

/**
 * Some utilities for Java {@link Unsafe}.
 */
public final class UnsafeUtil {

    private UnsafeUtil() {
        throw new UnsupportedOperationException();
    }

    private static Unsafe theUnsafe;

    public static Unsafe getTheUnsafe() throws Exception {
        if (theUnsafe == null) {
            theUnsafe = (Unsafe) ReflectionUtil.getDeclaredFieldValue(Unsafe.class, "theUnsafe", null);
            if (theUnsafe == null) {
                throw new IllegalStateException("Failed to get an instance of " + Unsafe.class.getName());
            }
        }
        return theUnsafe;
    }

    public static <T> T allocateInstance(Class<T> clazz) throws Exception {
        return (T) getTheUnsafe().allocateInstance(clazz);
    }

    public static void setStaticFieldByOffset(Field field, @Nullable Object value) throws Exception {
        Unsafe theUnsafe = getTheUnsafe();
        theUnsafe.putObject(theUnsafe.staticFieldBase(field), theUnsafe.staticFieldOffset(field), value);
    }

}
