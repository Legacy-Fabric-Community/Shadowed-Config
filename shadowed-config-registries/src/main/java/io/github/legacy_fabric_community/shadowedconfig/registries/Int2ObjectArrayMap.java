package io.github.legacy_fabric_community.shadowedconfig.registries;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import net.minecraft.util.registry.Registry;

@SuppressWarnings("unchecked")
// TODO: port this to scala
// TODO: the reason why its so hard is because of the arrays
public class Int2ObjectArrayMap<K> implements Registry<Integer, K> {
	private static final Object NULL = null;
	private K[] values;
	private int[] ids;
	private K[] idToValues;
	private int nextId;
	private int size;

	public Int2ObjectArrayMap(int i) {
		i = (int) ((float) i / 0.8F);
		this.values = (K[]) new Object[i];
		this.ids = new int[i];
		this.idToValues = (K[]) Lists.<K>newArrayListWithCapacity(i).toArray();
	}

	public int getId(@Nullable K object) {
		return this.getIdFromIndex(this.findIndex(object, this.getIdealIndex(object)));
	}

	@Nullable
	public K get(Integer index) {
		return index >= 0 && index < this.idToValues.length ? this.idToValues[index] : null;
	}

	private int getIdFromIndex(int i) {
		return i == -1 ? -1 : this.ids[i];
	}

	public int add(K object) {
		int i = this.nextId();
		this.put(i, object);
		return i;
	}

	private int nextId() {
		while (this.nextId < this.idToValues.length && this.idToValues[this.nextId] != null) {
			++this.nextId;
		}

		return this.nextId;
	}

	private void resize(int i) {
		K[] objects = this.values;
		int[] is = this.ids;
		this.values = (K[]) Lists.<K>newArrayListWithCapacity(i).toArray();
		this.ids = new int[i];
		this.idToValues = (K[]) Lists.<K>newArrayListWithCapacity(i).toArray();
		this.nextId = 0;
		this.size = 0;

		for (int j = 0; j < objects.length; ++j) {
			if (objects[j] != null) {
				this.put(is[j], objects[j]);
			}
		}

	}

	@Override
	public void put(Integer index, K object) {
		int j = Math.max(index, this.size + 1);
		int k;
		if ((float) j >= (float) this.values.length * 0.8F) {
			k = this.values.length << 1;
			while (k < 1) {
				k <<= 1;
			}
			this.resize(k);
		}

		k = this.findFree(this.getIdealIndex(object));
		this.values[k] = object;
		this.ids[k] = index;
		this.idToValues[index] = object;
		++this.size;
		if (index == this.nextId) {
			++this.nextId;
		}

	}

	private int getIdealIndex(@Nullable K object) {
		return (getIdealHash(System.identityHashCode(object)) & Integer.MAX_VALUE) % this.values.length;
	}

	public static int getIdealHash(int i) {
		i ^= i >>> 16;
		i *= -2048144789;
		i ^= i >>> 13;
		i *= -1028477387;
		i ^= i >>> 16;
		return i;
	}

	private int findIndex(@Nullable K object, int i) {
		int k;
		for (k = i; k < this.values.length; ++k) {
			if (this.values[k] == object) {
				return k;
			}

			if (this.values[k] == NULL) {
				return -1;
			}
		}

		for (k = 0; k < i; ++k) {
			if (this.values[k] == object) {
				return k;
			}

			if (this.values[k] == NULL) {
				return -1;
			}
		}

		return -1;
	}

	private int findFree(int i) {
		int k;
		for (k = i; k < this.values.length; ++k) {
			if (this.values[k] == NULL) {
				return k;
			}
		}

		for (k = 0; k < i; ++k) {
			if (this.values[k] == NULL) {
				return k;
			}
		}

		throw new RuntimeException("Overflowed :(");
	}

	@Nonnull
	public Iterator<K> iterator() {
		return Iterators.filter(Iterators.forArray(this.idToValues), Objects::nonNull);
	}

	public void clear() {
		Arrays.fill(this.values, null);
		Arrays.fill(this.idToValues, null);
		this.nextId = 0;
		this.size = 0;
	}

	public int size() {
		return this.size;
	}
}
