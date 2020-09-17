package io.github.legacy_fabric_community.serialization.util;

import java.util.function.Supplier;

public class Lazy<T> {
    private Supplier<T> delegate;
    private T value;

    private Lazy(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    public T get() {
        Supplier<T> supplier = this.delegate;
        if (supplier != null) {
            this.value = supplier.get();
            this.delegate = null;
        }
        return this.value;
    }

    public static <T> Lazy<T> of(T value) {
        return new Lazy<>(() -> value);
    }

    public static <T> Lazy<T> of(Supplier<T> value) {
        return new Lazy<>(value);
    }
}
