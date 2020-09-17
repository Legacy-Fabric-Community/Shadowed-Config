package io.github.legacy_fabric_community.serialization.registry;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface IdentifiableRegistry<T> extends Registry<Identifier, T> {
    T getRandom(Random random);

    Set<Identifier> getIds();

    int getRawId(@Nullable T entry);

    @Nullable
    Identifier getId(T entry);

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}
