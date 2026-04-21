package org.fiddlemc.fiddle.impl.util.java.reflect;

import org.jspecify.annotations.Nullable;
import java.lang.reflect.Field;

/**
 * Some utilities for Java reflection.
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Calls {@link Class#getDeclaredField} and {@link Field#setAccessible}{@code (true)}.
     *
     * @return The {@link Field}.
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * Calls {@link #getDeclaredField} followed by {@link Field#get}.
     *
     * @return The field's value.
     */
    public static Object getDeclaredFieldValue(Class<?> clazz, String fieldName, @Nullable Object obj) throws NoSuchFieldException, IllegalAccessException {
        return getDeclaredField(clazz, fieldName).get(obj);
    }

    /**
     * Calls {@link #getDeclaredField} followed by {@link Field#set}.
     */
    public static void setDeclaredFieldValue(Class<?> clazz, String fieldName, @Nullable Object obj, @Nullable Object value) throws NoSuchFieldException, IllegalAccessException {
        getDeclaredField(clazz, fieldName).set(obj, value);
    }

}
