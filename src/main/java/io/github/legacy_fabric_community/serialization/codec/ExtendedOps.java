package io.github.legacy_fabric_community.serialization.codec;

import java.util.function.Consumer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public interface ExtendedOps<T> extends DynamicOps<T> {
    default SetBuilder<T> setBuilder() {
        return new SetBuilder.Builder<>(this);
    }

    default DataResult<Consumer<Consumer<T>>> getSet(T input) {
        return this.getList(input);
    }
}
