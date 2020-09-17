package io.github.legacy_fabric_community.serialization.registry;

import net.minecraft.util.Identifier;

public abstract class MutableRegistry<T> implements IdentifiableRegistry<T> {
    public abstract <V extends T> V set(int rawId, Identifier id, V entry);

    public abstract <V extends T> V add(Identifier id, V entry);

    public abstract boolean isEmpty();
}
