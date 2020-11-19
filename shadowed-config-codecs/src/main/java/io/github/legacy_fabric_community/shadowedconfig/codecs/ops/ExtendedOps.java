package io.github.legacy_fabric_community.shadowedconfig.codecs.ops;

import java.util.function.Consumer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.legacy_fabric_community.shadowedconfig.codecs.SetBuilder;

public interface ExtendedOps<T> extends DynamicOps<T> {
	default SetBuilder<T> setBuilder() {
		return new SetBuilder.Builder<>(this);
	}

	default DataResult<Consumer<Consumer<T>>> getSet(T input) {
		return this.getList(input);
	}
}
