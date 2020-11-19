package io.github.legacy_fabric_community.shadowedconfig.registries;

import net.minecraft.util.Identifier;

public interface MutableRegistry<T> extends IdentifiableRegistry<T> {
	<V extends T> V set(int rawId, Identifier id, V entry);

	<V extends T> V add(Identifier id, V entry);

	boolean isEmpty();
}
