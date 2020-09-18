package io.github.legacy_fabric_community.serialization.mixin.mixin;

import io.github.legacy_fabric_community.serialization.codec.RegistryCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<K, V> implements Registry<K, V>, Codec<V> {
    @Shadow public abstract K getIdentifier(V object);

    @Shadow public abstract int getIndex(V object);

    @Shadow @Final protected IdList<V> ids;

    @Shadow public abstract V get(K key);

    @Override
    public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
        if (ops.compressMaps()) {
            return ops.getNumberValue(input).flatMap(number -> {
                V object = this.ids.fromId(number.intValue());
                return (object == null) ? DataResult.error("Unknown registry id: " + number) : DataResult.success(object, Lifecycle.stable());
            }).map(object -> Pair.of(object, ops.empty()));
        }
        // TODO: Is it safe to assume that K will always be an Identifier?
        return RegistryCodecs.IDENTIFIER.decode(ops, input).flatMap(pair -> {
            V object = this.get((K) pair.getFirst());
            return (object == null) ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(object, pair.getSecond()), Lifecycle.stable());
        });
    }

    @Override
    public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
        K identifier = this.getIdentifier(input);
        if (identifier == null)
            return DataResult.error("Unknown registry element " + input);
        if (ops.compressMaps())
            return ops.mergeToPrimitive(prefix, ops.createInt(this.getIndex(input))).setLifecycle(Lifecycle.experimental());
        return ops.mergeToPrimitive(prefix, ops.createString(identifier.toString())).setLifecycle(Lifecycle.experimental());
    }
}
