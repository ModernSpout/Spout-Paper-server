package org.fiddlemc.fiddle.impl.util.composable;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * An implementation of {@link KeyedBuilderComposeEventImpl} that includes
 * a {@link ClientView.AwarenessLevel} and another int-like half as part of a {@link Pair} key.
 */
public abstract class AwarenessLevelPairKeyedBuilderComposeEventImpl<K, E, B> extends KeyedBuilderComposeEventImpl<Pair<ClientView.AwarenessLevel, K>, E, B, IntIntPair> {

    protected abstract int keyPartToInt(K key);

    protected abstract K intToKeyPart(int internalKey);

    @Override
    protected IntIntPair keyToInternalKey(final Pair<ClientView.AwarenessLevel, K> key) {
        return IntIntPair.of(key.left().ordinal(), this.keyPartToInt(key.right()));
    }

    @Override
    protected Pair<ClientView.AwarenessLevel, K> internalKeyToKey(final IntIntPair internalKey) {
        return Pair.of(ClientView.AwarenessLevel.getAll()[internalKey.leftInt()], this.intToKeyPart(internalKey.rightInt()));
    }

    public void register(Collection<ClientView.AwarenessLevel> awarenessLevels, Collection<K> keys, E element) {
        List<Pair<ClientView.AwarenessLevel, K>> pairKeys = new ArrayList<>(awarenessLevels.size() * keys.size());
        for (ClientView.AwarenessLevel awarenessLevel : awarenessLevels) {
            for (K key : keys) {
                pairKeys.add(Pair.of(awarenessLevel, key));
            }
        }
        this.register(pairKeys, element);
    }

    public void copyRegisteredInvertedAndReinvertedInto(E[][][] array, IntFunction<E[]> arrayGenerator) {
        for (Map.Entry<List<E>, List<IntIntPair>> entry : this.getRegisteredInvertedWithInternalKey().entrySet()) {
            for (IntIntPair target : entry.getValue()) {
                array[target.firstInt()][target.secondInt()] = entry.getKey().toArray(arrayGenerator);
            }
        }
    }

}
