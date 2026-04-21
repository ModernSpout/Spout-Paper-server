package org.fiddlemc.fiddle.impl.packetmapping.item.reverse;

import com.google.common.cache.LoadingCache;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

/**
 * A class, bound to a particular client session,
 * that allows for the reversing of the mapping of item stacks sent to the client.
 * In other words, it allows for the mapping of the data sent to the client (or received from it)
 * back to the original server-side data.
 */
public class ItemMappingReverser {

    private static final String ID_TAG_KEY = "fiddle_server_side_item";
    private static final long MAX_REMEMBER_MAPPING_TIME = 600_000_000_000L; // 10 minutes
    private static final int MAX_REMEMBER_MAPPING_COUNT = 10_000;
    private static final long INITIAL_TIME = System.nanoTime();

    /**
     * The cached return value for {@link #tagOps},
     * or null if not cached yet.
     */
    private static @Nullable DynamicOps<Tag> TAG_OPS;

    private static DynamicOps<Tag> tagOps() {
        if (TAG_OPS == null) {
            TAG_OPS = MinecraftServer.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        }
        return TAG_OPS;
    }

    /**
     * The cached return value fo {@link #hashGenerator},
     * or null if not cached yet.
     */
    private static HashedPatchMap.@Nullable HashGenerator HASH_GENERATOR;

    private static HashedPatchMap.HashGenerator hashGenerator() {
        if (HASH_GENERATOR == null) {
            LoadingCache<TypedDataComponent<?>, Integer> loadingCache = ServerPlayer.createHashingCache(MinecraftServer.getServer().registryAccess(), 32768L);
            HASH_GENERATOR = loadingCache::getUnchecked;
        }
        return HASH_GENERATOR;
    }

    private final Semaphore lock = new Semaphore(1);
    private long nextId = 0;

    /**
     * All current {@link ExistingMapping} by their {@link ExistingMapping#id}.
     */
    private final Long2ObjectMap<ExistingMapping> existingMappingsById = new Long2ObjectOpenHashMap<>();

    /**
     * All current {@link ExistingMapping}s sorted by their {@link ExistingMapping#timeLastUsed}.
     */
    private final TreeSet<ExistingMapping> existingMappingsByTimeLastUsed = new TreeSet<>(
        Comparator.comparingLong((ExistingMapping mapping) -> mapping.timeLastUsed).thenComparingLong(mapping -> mapping.id)
    );

    /**
     * All current {@link ExistingMapping}s by their {@link ExistingMapping#sentWithCount1Tag}.
     */
    private final Map<Tag, List<ExistingMapping>> existingMappingsBySentWithCount1Tag = new HashMap<>();

    /**
     * All current {@link ExistingMapping}s by their {@link ExistingMapping#sentAfterAddingIdWithCount1HashedKey}.
     */
    private final Map<HashedStackKey, List<ExistingMapping>> existingMappingsBySentAfterAddingIdWithCount1HashedKey = new TreeMap<>();

    private static class ExistingMapping {

        final long id;

        /**
         * The last {@link System#nanoTime()} at which this instance was accessed,
         * minus the {@link #INITIAL_TIME}.
         */
        long timeLastUsed;

        /**
         * The server-side item stack, with {@linkplain ItemStack#getCount() count} 1.
         */
        final ItemStack serverSideWithCount1;

        /**
         * {@link #serverSideWithCount1} as a {@link HashedStackKey}.
         */
        final HashedStackKey serverSideWithCount1HashedKey;

        /**
         * {@link #serverSideWithCount1} as a {@link Tag}.
         */
        final Tag serverSideWithCount1Tag;

        /**
         * The item stack sent to the client
         * before adding the id tag, with {@linkplain ItemStack#getCount() count} 1, as a {@link Tag}.
         */
        final Tag sentWithCount1Tag;

        /**
         * The item stack sent to the client after adding the id tag,
         * with {@linkplain ItemStack#getCount() count} 1, as a {@link HashedStackKey},
         * or null if not {@linkplain #setSentAfterAddingIdWithCount1 set} yet.
         */
        @Nullable HashedStackKey sentAfterAddingIdWithCount1HashedKey;

        ExistingMapping(long id, ItemStack serverSideWithCount1, Tag serverSideWithCount1Tag, Tag sentWithCount1Tag) {
            this.id = id;
            this.setTimeLastUsed();
            this.serverSideWithCount1 = serverSideWithCount1;
            this.serverSideWithCount1HashedKey = HashedStackKey.of(serverSideWithCount1);
            this.serverSideWithCount1Tag = serverSideWithCount1Tag;
            this.sentWithCount1Tag = sentWithCount1Tag;
        }

        void setSentAfterAddingIdWithCount1(ItemStack sentAfterAddingIdWithCount1) {
            this.sentAfterAddingIdWithCount1HashedKey = HashedStackKey.of(sentAfterAddingIdWithCount1);
        }

        private void setTimeLastUsed() {
            this.timeLastUsed = System.nanoTime() - INITIAL_TIME;
        }

        void updateTimeLastUsed(ItemMappingReverser reverser) {
            reverser.existingMappingsByTimeLastUsed.remove(this);
            this.setTimeLastUsed();
            reverser.existingMappingsByTimeLastUsed.add(this);
        }

        void removeFromAllExceptByTimeLastUsed(ItemMappingReverser reverser) {
            reverser.existingMappingsById.remove(this.id);
            reverser.existingMappingsBySentWithCount1Tag.computeIfPresent(this.sentWithCount1Tag, ($, mappings) -> {
                mappings.remove(this);
                return mappings.isEmpty() ? null : mappings;
            });
            if (this.sentAfterAddingIdWithCount1HashedKey != null) {
                reverser.existingMappingsBySentAfterAddingIdWithCount1HashedKey.computeIfPresent(this.sentAfterAddingIdWithCount1HashedKey, ($, mappings) -> {
                    mappings.remove(this);
                    return mappings.isEmpty() ? null : mappings;
                });
            }
        }

    }

    private static class HashedStackKey implements Comparable<HashedStackKey> {

        final Item item;
        final Holder<Item> itemHolder;
        final HashedPatchMap hashedPatchmap;

        HashedStackKey(Item item, Holder<Item> itemHolder, HashedPatchMap hashedPatchmap) {
            this.item = item;
            this.itemHolder = itemHolder;
            this.hashedPatchmap = hashedPatchmap;
        }

        @Override
        public int compareTo(HashedStackKey o) {
            int compare = Integer.compare(this.item.indexInItemRegistry, o.item.indexInItemRegistry);
            if (compare != 0) {
                return compare;
            }
            int addedSize = this.hashedPatchmap.addedComponents().size();
            compare = Integer.compare(addedSize, o.hashedPatchmap.addedComponents().size());
            if (compare != 0) {
                return compare;
            }
            int removedSize = this.hashedPatchmap.removedComponents().size();
            compare = Integer.compare(removedSize, o.hashedPatchmap.removedComponents().size());
            if (compare != 0) {
                return compare;
            }
            List<DataComponentType<?>> types1 = this.hashedPatchmap.addedComponents().keySet().stream().sorted(Comparator.comparingInt(BuiltInRegistries.DATA_COMPONENT_TYPE::getId)).toList();
            List<DataComponentType<?>> types2 = o.hashedPatchmap.addedComponents().keySet().stream().sorted(Comparator.comparingInt(BuiltInRegistries.DATA_COMPONENT_TYPE::getId)).toList();
            for (int i = 0; i < addedSize; i++) {
                DataComponentType<?> type1 = types1.get(i);
                DataComponentType<?> type2 = types2.get(i);
                int id1 = BuiltInRegistries.DATA_COMPONENT_TYPE.getId(type1);
                int id2 = BuiltInRegistries.DATA_COMPONENT_TYPE.getId(type2);
                compare = Integer.compare(id1, id2);
                if (compare != 0) {
                    return compare;
                }
                int val1 = this.hashedPatchmap.addedComponents().get(type1);
                int val2 = o.hashedPatchmap.addedComponents().get(type2);
                compare = Integer.compare(val1, val2);
                if (compare != 0) {
                    return compare;
                }
            }
            types1 = this.hashedPatchmap.removedComponents().stream().sorted(Comparator.comparingInt(BuiltInRegistries.DATA_COMPONENT_TYPE::getId)).toList();
            types2 = o.hashedPatchmap.removedComponents().stream().sorted(Comparator.comparingInt(BuiltInRegistries.DATA_COMPONENT_TYPE::getId)).toList();
            for (int i = 0; i < removedSize; i++) {
                DataComponentType<?> type1 = types1.get(i);
                DataComponentType<?> type2 = types2.get(i);
                int id1 = BuiltInRegistries.DATA_COMPONENT_TYPE.getId(type1);
                int id2 = BuiltInRegistries.DATA_COMPONENT_TYPE.getId(type2);
                compare = Integer.compare(id1, id2);
                if (compare != 0) {
                    return compare;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof HashedStackKey o && this.compareTo(o) == 0;
        }

        HashedStack withCount(int count) {
            return new HashedStack.ActualItem(this.itemHolder, count, this.hashedPatchmap);
        }

        static HashedStackKey of(HashedStack.ActualItem actualItem) {
            return new HashedStackKey(actualItem.item().value(), actualItem.item(), actualItem.components());
        }

        static HashedStackKey of(ItemStack itemStack) {
            return new HashedStackKey(itemStack.getItem(), itemStack.getItemHolder(), HashedPatchMap.create(itemStack.getComponentsPatch(), hashGenerator()));
        }

    }

    private void removeId(ItemStack itemStack) {
        @Nullable CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag existingTag = customData.getUnsafe();
            if (existingTag.get(ID_TAG_KEY) instanceof LongTag) {
                itemStack.set(DataComponents.CUSTOM_DATA, itemStack.get(DataComponents.CUSTOM_DATA).update(tag -> tag.remove(ID_TAG_KEY)));
            }
        }
    }

    public void attachId(ItemStack itemStack, long id) {
        @Nullable CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            customData = CustomData.of(new CompoundTag());
        }
        LongTag idTag = LongTag.valueOf(id);
        CustomData newCustomData = customData.update(tag -> tag.put(ID_TAG_KEY, idTag));
        itemStack.set(DataComponents.CUSTOM_DATA, newCustomData);
    }

    public long reserveAndAttachId(ItemStack sent, ItemStack serverSide) {

        // Remove the id if present
        this.removeId(sent);

        long id = -1;
        try {
            this.lock.acquireUninterruptibly();

            // Find an existing id for this stack
            ItemStack sentWithCount1 = sent.copyWithCount(1);
            Tag sentWithCount1Tag = ItemStack.CODEC.encodeStart(tagOps(), sentWithCount1).getOrThrow();
            ItemStack serverSideWithCount1 = serverSide.copyWithCount(1);
            Tag serverSideWithCount1Tag = ItemStack.CODEC.encodeStart(tagOps(), serverSideWithCount1).getOrThrow();
            List<ExistingMapping> existingMappings = this.existingMappingsBySentWithCount1Tag.computeIfAbsent(sentWithCount1Tag, $ -> new ArrayList<>(1));
            for (ExistingMapping existingMapping : existingMappings) {
                if (existingMapping.serverSideWithCount1Tag.equals(serverSideWithCount1Tag)) {
                    // existingMapping.updateTimeLastUsed(this);
                    id = existingMapping.id;
                }
            }

            // Reserve a new id
            if (id == -1) {
                id = this.nextId++;
                this.attachId(sent, id);
                ExistingMapping existingMapping = new ExistingMapping(id, serverSideWithCount1, serverSideWithCount1Tag, sentWithCount1Tag);
                this.existingMappingsById.put(id, existingMapping);
                this.existingMappingsByTimeLastUsed.add(existingMapping);
                return id;
            }

        } finally {
            this.lock.release();
        }

        // For existing ids, we just attach outside of the critical section
        this.attachId(sent, id);
        return id;

    }

    public void storeAsReversible(long id, ItemStack sentAfterAddingId) {
        try {
            this.lock.acquireUninterruptibly();

            ExistingMapping existingMapping = this.existingMappingsById.get(id);
            if (existingMapping == null) {
                // The mapping got deleted already, ignore this one
                return;
            }

            ItemStack sentAfterAddingIdWithCount1 = sentAfterAddingId.copyWithCount(1);
            existingMapping.setSentAfterAddingIdWithCount1(sentAfterAddingIdWithCount1);
            this.existingMappingsBySentWithCount1Tag.computeIfAbsent(existingMapping.sentWithCount1Tag, $ -> new ArrayList<>(1)).add(existingMapping);
            this.existingMappingsBySentAfterAddingIdWithCount1HashedKey.computeIfAbsent(existingMapping.sentAfterAddingIdWithCount1HashedKey, $ -> new ArrayList<>(1)).add(existingMapping);
            existingMapping.updateTimeLastUsed(this);

            // Do some maintenance
            this.removeOutdatedMappings();
            this.removeTooManyMappings();

        } finally {
            this.lock.release();
        }
    }

    private void removeOutdatedMappings() {
        // Remove mappings if outdated or too many
        while (this.existingMappingsByTimeLastUsed.size() >= MAX_REMEMBER_MAPPING_COUNT) {
            ExistingMapping firstMapping = this.existingMappingsByTimeLastUsed.pollFirst();
            firstMapping.removeFromAllExceptByTimeLastUsed(this);
        }
    }

    private void removeTooManyMappings() {
        while (!this.existingMappingsByTimeLastUsed.isEmpty()) {
            ExistingMapping firstMapping = this.existingMappingsByTimeLastUsed.first();
            if (System.nanoTime() - INITIAL_TIME - firstMapping.timeLastUsed > MAX_REMEMBER_MAPPING_TIME) {
                this.existingMappingsByTimeLastUsed.pollFirst();
                firstMapping.removeFromAllExceptByTimeLastUsed(this);
            } else {
                break;
            }
        }
    }

    /**
     * Reverses the mapping that was applied to the {@code received} item stack.
     *
     * @param received The {@link ItemStack} that was received from a client.
     * @return The original server-side {@link ItemStack},
     * or {@code received} if none could be determined.
     */
    public ItemStack reverseMappingIfPossible(ItemStack received) {
        @Nullable CustomData customData = received.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            Tag idTag = customData.getUnsafe().get(ID_TAG_KEY);
            if (idTag instanceof LongTag longTag) {
                long id = longTag.longValue();
                @Nullable ExistingMapping existingMapping;
                this.lock.acquireUninterruptibly();
                try {
                    existingMapping = this.existingMappingsById.get(id);
                    if (existingMapping != null) {
                        existingMapping.updateTimeLastUsed(this);
                    }
                } finally {
                    this.lock.release();
                }
                if (existingMapping != null) {
                    return existingMapping.serverSideWithCount1.copyWithCount(received.getCount());
                }
            }
        }
        return received;
    }

    /**
     * Reverses the mapping that was applied to the {@code received} hashed item stack.
     *
     * @param received The {@link HashedStack} that was received from a client.
     * @return The original server-side {@link HashedStack},
     * or {@code received} if none could be determined.
     */
    public HashedStack reverseMappingIfPossible(HashedStack received) {
        if (received instanceof HashedStack.ActualItem actualItem) {
            HashedStackKey lookupKey = HashedStackKey.of(actualItem);
            @Nullable HashedStackKey serverSideHashedKey = null;
            this.lock.acquireUninterruptibly();
            try {
                @Nullable List<ExistingMapping> existingMappings = this.existingMappingsBySentAfterAddingIdWithCount1HashedKey.get(lookupKey);
                if (existingMappings != null && !existingMappings.isEmpty()) {
                    serverSideHashedKey = existingMappings.get(existingMappings.size() - 1).serverSideWithCount1HashedKey;
                }
            } finally {
                this.lock.release();
            }
            if (serverSideHashedKey != null) {
                return serverSideHashedKey.withCount(actualItem.count());
            }
        }
        return received;
    }

}
