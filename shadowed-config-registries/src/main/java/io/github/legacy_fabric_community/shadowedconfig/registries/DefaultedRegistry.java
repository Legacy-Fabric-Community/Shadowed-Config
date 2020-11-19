package io.github.legacy_fabric_community.shadowedconfig.registries;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.Identifier;

public class DefaultedRegistry<T> extends Registry<T> {
	private final Identifier defaultId;
	private T defaultValue;

	public DefaultedRegistry(String defaultId) {
		this(new Identifier(defaultId));
	}

	public DefaultedRegistry(Identifier defaultId) {
		this.defaultId = defaultId;
	}

	@Override
	public <V extends T> V set(int rawId, Identifier id, V entry) {
		if (this.defaultId.equals(id)) {
			this.defaultValue = entry;
		}

		return super.set(rawId, id, entry);
	}

	@Override
	public int getRawId(@Nullable T entry) {
		int i = super.getRawId(entry);
		return i == -1 ? super.getRawId(this.defaultValue) : i;
	}

	@Override
	@Nonnull
	public Identifier getId(T entry) {
		Identifier identifier = super.getId(entry);
		return identifier == null ? this.defaultId : identifier;
	}

	@Override
	@Nonnull
	public T get(@Nullable Identifier id) {
		T object = super.get(id);
		return object == null ? this.defaultValue : object;
	}

	@Override
	@Nonnull
	public T get(int index) {
		T object = super.get(index);
		return object == null ? this.defaultValue : object;
	}

	@Override
	@Nonnull
	public T getRandom(Random random) {
		T object = super.getRandom(random);
		return object == null ? this.defaultValue : object;
	}

	public Identifier getDefaultId() {
		return this.defaultId;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}
}
