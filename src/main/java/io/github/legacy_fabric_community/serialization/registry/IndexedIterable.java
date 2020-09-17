package io.github.legacy_fabric_community.serialization.registry;

import javax.annotation.Nullable;

public interface IndexedIterable<T> extends Iterable<T> {
    @Nullable
    T get(int index);
}
