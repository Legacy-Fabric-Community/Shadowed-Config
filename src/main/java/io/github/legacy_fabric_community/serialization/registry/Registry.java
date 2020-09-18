package io.github.legacy_fabric_community.serialization.registry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.legacy_fabric_community.serialization.codec.RegistryCodecs;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.util.Identifier;

@SuppressWarnings("unchecked")
public class Registry<T> implements MutableRegistry<T>, Codec<T> {
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
    public static final MutableRegistry<MutableRegistry<?>> REGISTRIES = new Registry<>();
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Int2ObjectArrayMap<T> indexedEntries = new Int2ObjectArrayMap<>(256);
    protected final BiMap<Identifier, T> entries = HashBiMap.create();
    protected Object[] randomEntries;
    protected Lifecycle lifecycle = Lifecycle.stable();
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

    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Lifecycle getLifecycle() {
        return this.lifecycle;
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

    // TODO: Lifecycles for each registry entry
    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        if (ops.compressMaps())
            return ops.getNumberValue(input).flatMap(number -> {
                T object = this.get(number.intValue());
                return (object == null) ? DataResult.error("Unknown registry id: " + number) : DataResult.success(object, Lifecycle.stable());
            }).map(object -> Pair.of(object, ops.empty()));
        return RegistryCodecs.IDENTIFIER.decode(ops, input).flatMap(pair -> {
            T object = this.get(pair.getFirst());
            return (object == null) ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(object, pair.getSecond()), Lifecycle.stable());
        });
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        Identifier identifier = this.getId(input);
        if (identifier == null)
            return DataResult.error("Unknown registry element " + input);
        if (ops.compressMaps())
            return ops.mergeToPrimitive(prefix, ops.createInt(this.getRawId(input))).setLifecycle(this.lifecycle);
        return ops.mergeToPrimitive(prefix, ops.createString(identifier.toString())).setLifecycle(this.lifecycle);
    }
}
