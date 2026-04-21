package org.fiddlemc.fiddle.impl.util.composable;

import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import it.unimi.dsi.fastutil.Pair;
import org.fiddlemc.fiddle.api.util.composable.BuilderComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.ChangeRegisteredComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.GetRegisteredComposeEvent;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An implementation of {@link BuilderComposeEvent} where all registered elements
 * are registered by a {@linkplain K key}.
 */
public abstract class KeyedBuilderComposeEventImpl<K, E, B, IK> implements PaperLifecycleEvent, BuilderComposeEvent<B>, GetRegisteredComposeEvent<K, E>, ChangeRegisteredComposeEvent<K, E> {

    /**
     * The registered elements, by their internal key.
     *
     * <p>
     * The map may not contain a value for each key,
     * but it never contains an empty list.
     * </p>
     */
    private final Map<IK, List<E>> elements = new HashMap<>();

    protected abstract IK keyToInternalKey(K key);

    protected abstract K internalKeyToKey(IK internalKey);

    @Override
    public List<E> getRegistered(K key) {
        @Nullable List<E> original = this.elements.get(this.keyToInternalKey(key));
        return original != null ? Collections.unmodifiableList(original) : Collections.emptyList();
    }

    @Override
    public void changeRegistered(K key, Consumer<List<E>> listConsumer) {
        IK internalKey = this.keyToInternalKey(key);
        @Nullable List<E> original = this.elements.get(internalKey);
        List<E> passed = original != null ? original : new ArrayList<>(1);
        listConsumer.accept(passed);
        if (passed.isEmpty()) {
            if (original != null) {
                this.elements.remove(internalKey);
            }
        } else {
            if (original == null) {
                this.elements.put(internalKey, passed);
            }
        }
    }

    public void register(Iterable<K> keys, E element) {
        for (K key : keys) {
            IK internalKey = this.keyToInternalKey(key);
            this.elements.computeIfAbsent(internalKey, $ -> new ArrayList<>(1)).add(element);
        }
    }

    public List<Pair<K, List<E>>> getRegistered() {
        return this.elements.entrySet().stream().map(entry -> Pair.of(this.internalKeyToKey(entry.getKey()), entry.getValue())).toList();
    }

    public List<Pair<IK, List<E>>> getRegisteredWithInternalKey() {
        return this.elements.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList();
    }

    public Map<List<E>, List<K>> getRegisteredInverted() {
        Map<List<E>, List<K>> inverted = new HashMap<>(this.elements.size());
        for (Map.Entry<IK, List<E>> entry : this.elements.entrySet()) {
            inverted.computeIfAbsent(entry.getValue(), $ -> new ArrayList<>(1)).add(this.internalKeyToKey(entry.getKey()));
        }
        return inverted;
    }

    public Map<List<E>, List<IK>> getRegisteredInvertedWithInternalKey() {
        Map<List<E>, List<IK>> inverted = new HashMap<>(this.elements.size());
        for (Map.Entry<IK, List<E>> entry : this.elements.entrySet()) {
            inverted.computeIfAbsent(entry.getValue(), $ -> new ArrayList<>(1)).add(entry.getKey());
        }
        return inverted;
    }

}
