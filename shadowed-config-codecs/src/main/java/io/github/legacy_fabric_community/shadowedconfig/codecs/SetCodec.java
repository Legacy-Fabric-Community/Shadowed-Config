package io.github.legacy_fabric_community.shadowedconfig.codecs;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import io.github.legacy_fabric_community.shadowedconfig.codecs.ops.ExtendedOps;
import org.apache.commons.lang3.mutable.MutableObject;

public class SetCodec<A> implements Codec<Set<A>> {
	private final Codec<A> elementCodec;

	protected SetCodec(Codec<A> elementCodec) {
		this.elementCodec = elementCodec;
	}

	@Override
	public <T> DataResult<Pair<Set<A>, T>> decode(DynamicOps<T> ops, T input) {
		if (!(ops instanceof ExtendedOps)) {
			throw new UnsupportedOperationException("Only supported on ExtendedOps! Please use NbtOps, JanksonOps, or any other ExtendedOps implementation instead!");
		}
		return ((ExtendedOps<T>) ops).getSet(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
			ImmutableSet.Builder<A> read = ImmutableSet.builder();
			Stream.Builder<T> failed = Stream.builder();
			MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

			stream.accept(t -> {
				DataResult<Pair<A, T>> element = this.elementCodec.decode(ops, t);
				element.error().ifPresent(e -> failed.add(t));
				result.setValue(result.getValue().apply2stable((r, v) -> {
					read.add(v.getFirst());
					return r;
				}, element));
			});

			ImmutableSet<A> elements = read.build();
			T errors = ops.createList(failed.build());

			Pair<Set<A>, T> pair = Pair.of(elements, errors);

			return result.getValue().map(unit -> pair).setPartial(pair);
		});
	}

	@Override
	public <T> DataResult<T> encode(Set<A> input, DynamicOps<T> ops, T prefix) {
		if (!(ops instanceof ExtendedOps)) {
			throw new UnsupportedOperationException("Only supported on ExtendedOps! Please use NbtOps, JanksonOps, or any other ExtendedOps implementation instead!!");
		}
		SetBuilder<T> builder = ((ExtendedOps<T>) ops).setBuilder();

		for (A a : input) {
			builder.add(this.elementCodec.encodeStart(ops, a));
		}

		return builder.build(prefix);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().isAssignableFrom(o.getClass())) {
			return false;
		}
		if (super.equals(o)) {
			return true;
		}
		SetCodec<?> setCodec = (SetCodec<?>) o;
		return Objects.equals(this.elementCodec, setCodec.elementCodec);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.elementCodec);
	}

	@Override
	public String toString() {
		return "SetCodec[" + this.elementCodec + ']';
	}

	public static <A> SetCodec<A> of(Codec<A> elementCodec) {
		return new SetCodec<>(elementCodec);
	}
}
