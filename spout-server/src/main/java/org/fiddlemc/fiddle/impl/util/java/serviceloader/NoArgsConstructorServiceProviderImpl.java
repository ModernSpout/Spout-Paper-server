package org.fiddlemc.fiddle.impl.util.java.serviceloader;

import java.lang.reflect.Constructor;

/**
 * A simple implementation of {@link GenericServiceProviderImpl}
 * that creates new instances by calling the declared no-args constructor.
 */
public abstract class NoArgsConstructorServiceProviderImpl<S, I extends S> extends GenericServiceProviderImpl<S, I> {

    protected NoArgsConstructorServiceProviderImpl(Class<I> clazz) {
        super(clazz);
    }

    @Override
    protected I create() {
        try {
            Constructor constructor = this.clazz.getDeclaredConstructor();
            constructor.trySetAccessible();
            return (I) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
