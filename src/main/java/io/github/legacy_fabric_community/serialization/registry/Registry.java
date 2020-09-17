package io.github.legacy_fabric_community.serialization.registry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.util.Identifier;

@SuppressWarnings("unchecked")
public class Registry<T> extends MutableRegistry<T> {
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
    public static final MutableRegistry<MutableRegistry<?>> REGISTRIES = new Registry<>();
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap<>(256);
    protected final BiMap<Identifier, T> entries = HashBiMap.create();
    protected Object[] randomEntries;
    private int nextId;

    public boolean containsId(Identifier id) {
        return this.entries.containsKey(id);
    }

    @Override
    public <V extends T> V set(int rawId, Identifier id, V entry) {
        this.indexedEntries.put(entry, rawId);
        Validate.notNull(id);
        Validate.notNull(entry);
        this.randomEntries = null;
        this.entries.put(id, entry);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }

        return entry;
    }

    @Override
    public <V extends T> V add(Identifier id, V entry) {
        return this.set(this.nextId, id, entry);
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Collection<?> collection = this.entries.values();
            if (collection.isEmpty()) {
                return null;
            }

            this.randomEntries = collection.toArray(new Object[0]);
        }

        return (T) this.randomEntries[random.nextInt(this.randomEntries.length)];
    }

    @Override
    public ImmutableSet<Identifier> getIds() {
        return ImmutableSet.copyOf(this.entries.keySet());
    }

    @Override
    @Nullable
    public Identifier getId(T entry) {
        return this.entries.inverse().get(entry);
    }

    @Override
    public int getRawId(@Nullable T entry) {
        return this.indexedEntries.getId(entry);
    }

    @Override
    public T get(Identifier key) {
        return this.entries.get(key);
    }

    public T get(int index) {
        return this.indexedEntries.get(index);
    }

    @Override
    public void put(Identifier key, T value) {
        this.add(key, value);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }
}
