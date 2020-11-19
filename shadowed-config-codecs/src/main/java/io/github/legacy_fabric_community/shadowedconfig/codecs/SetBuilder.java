package io.github.legacy_fabric_community.shadowedconfig.codecs;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;

public interface SetBuilder<T> {
	DynamicOps<T> ops();

	DataResult<T> build(T prefix);

	SetBuilder<T> add(final T value);

	SetBuilder<T> add(final DataResult<T> value);

	SetBuilder<T> withErrorsFrom(final DataResult<?> result);

	SetBuilder<T> mapError(UnaryOperator<String> onError);

	default DataResult<T> build(final DataResult<T> prefix) {
		return prefix.flatMap(this::build);
	}

	default <E> SetBuilder<T> add(final E value, final Encoder<E> encoder) {
		return this.add(encoder.encodeStart(this.ops(), value));
	}

	default <E> SetBuilder<T> addAll(final Iterable<E> values, final Encoder<E> encoder) {
		values.forEach(v -> encoder.encode(v, this.ops(), this.ops().empty()));
		return this;
	}

	class Builder<T> implements SetBuilder<T> {
		private final DynamicOps<T> ops;
		private DataResult<ImmutableSet.Builder<T>> builder = DataResult.success(ImmutableSet.builder(), Lifecycle.stable());

		public Builder(final DynamicOps<T> ops) {
			this.ops = ops;
		}

		@Override
		public DynamicOps<T> ops() {
			return this.ops;
		}

		@Override
		public SetBuilder<T> add(final T value) {
			this.builder = this.builder.map(b -> b.add(value));
			return this;
		}

		@Override
		public SetBuilder<T> add(final DataResult<T> value) {
			this.builder = this.builder.apply2stable(ImmutableSet.Builder::add, value);
			return this;
		}

		@Override
		public SetBuilder<T> withErrorsFrom(final DataResult<?> result) {
			this.builder = this.builder.flatMap(r -> result.map(v -> r));
			return this;
		}

		@Override
		public SetBuilder<T> mapError(final UnaryOperator<String> onError) {
			this.builder = this.builder.mapError(onError);
			return this;
		}

		@Override
		public DataResult<T> build(final T prefix) {
			final DataResult<T> result = this.builder.flatMap(b -> this.ops.mergeToList(prefix, new ArrayList<T>(b.build())));
			this.builder = DataResult.success(ImmutableSet.builder(), Lifecycle.stable());
			return result;
		}
	}
}
