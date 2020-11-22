package io.github.legacy_fabric_community.shadowedconfig.codecs;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

public class ImmutableSetBuilder<T> implements SetBuilder<T> {
	private final DynamicOps<T> ops;
	private DataResult<ImmutableSet.Builder<T>> builder = DataResult.success(ImmutableSet.builder(), Lifecycle.stable());

	public ImmutableSetBuilder(final DynamicOps<T> ops) {
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
