package org.fiddlemc.fiddle.impl.util.java.enuminjection;

import org.fiddlemc.fiddle.impl.util.java.function.ConsumerThrowsException;
import org.fiddlemc.fiddle.impl.util.java.reflect.ReflectionUtil;
import org.fiddlemc.fiddle.impl.util.java.unsafe.UnsafeUtil;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract base for an injector that can inject additional values into an enum during runtime.
 *
 * <p>
 * First, call {@link #stage} once to prepare each desired value,
 * then call {@link #commit} to apply the changes.
 * </p>
 */
public abstract class EnumInjector<E extends Enum<E>> {

    private final Field ordinalField;
    private final Field nameField;
    private final Field valuesField;
    private final Field enumConstantsField;
    private final Field enumConstantDirectoryField;

    protected final Class<E> clazz;

    private final List<StagedInjection<E>> stagedInjections = new ArrayList<>();

    public EnumInjector(Class<E> clazz) throws Exception {
        this.ordinalField = ReflectionUtil.getDeclaredField(Enum.class, "ordinal");
        this.nameField = ReflectionUtil.getDeclaredField(Enum.class, "name");
        this.valuesField = ReflectionUtil.getDeclaredField(clazz, "$VALUES");
        this.enumConstantsField = ReflectionUtil.getDeclaredField(Class.class, "enumConstants");
        this.enumConstantDirectoryField = ReflectionUtil.getDeclaredField(Class.class, "enumConstantDirectory");
        this.clazz = clazz;
    }

    /**
     * Stages a new instance to be added to the enum.
     *
     * <p>
     * This does not mutate the enum yet.
     * </p>
     *
     * <p>
     * {@link #commit} must be called on this injector instance afterward to actually add the instance,
     * i.e. after all desired instances have been staged by calling this method multiple times.
     * </p>
     *
     * @param enumName   The {@linkplain Enum#name() name} of the instance.
     * @param onAllocate Extra processing to apply after allocating the instance.
     */
    protected void stage(String enumName, @Nullable ConsumerThrowsException<E, Exception> onAllocate) {
        this.stagedInjections.add(new StagedInjection<>(enumName, onAllocate));
    }

    /**
     * Overridable submethod for {@link #commit}.
     */
    protected void processStagedInjections() throws Exception {

        // Get the original values and prepare the new values array
        E[] originalValues = this.clazz.getEnumConstants();
        E[] newValues = Arrays.copyOf(originalValues, originalValues.length + this.stagedInjections.size());

        for (int i = 0; i < this.stagedInjections.size(); i++) {
            StagedInjection<E> stagedInjection = this.stagedInjections.get(i);
            // Allocate a value
            E value = UnsafeUtil.allocateInstance(this.clazz);
            // Determine the ordinal
            int ordinal = originalValues.length + i;
            // Set its ordinal()
            this.ordinalField.set(value, ordinal);
            // Set its name()
            this.nameField.set(value, stagedInjection.enumName);
            // Perform additional processing
            if (stagedInjection.onAllocate != null) {
                stagedInjection.onAllocate.accept(value);
            }
            // Add to the new values array
            newValues[ordinal] = value;
        }

        // Replace values()
        UnsafeUtil.setStaticFieldByOffset(this.valuesField, newValues);
        // Clear the enum constants to force an update of valueOf()
        this.enumConstantsField.set(this.clazz, null);
        this.enumConstantDirectoryField.set(this.clazz, null);

    }

    /**
     * Commits all {@linkplain #stage staged} additions.
     */
    public final void commit() throws Exception {
        // Skip if nothing is staged
        if (this.stagedInjections.isEmpty()) {
            return;
        }
        // Process the staged injections
        this.processStagedInjections();
        // Clear the staged injections
        this.stagedInjections.clear();
    }

    private record StagedInjection<T extends Enum<T>>(String enumName,
                                                      @Nullable ConsumerThrowsException<T, Exception> onAllocate) {
    }

}
