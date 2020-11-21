package io.github.legacy_fabric_community.shadowedconfig.registries

import java.util
import java.util.{Optional, Random}

import com.google.common.collect.{BiMap, HashBiMap, ImmutableSet, Maps}
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.{Codec, DataResult, DynamicOps, Lifecycle}
import io.github.legacy_fabric_community.shadowedconfig.codecs.StringMappedCodecs
import javax.annotation.{Nonnull, Nullable}
import net.minecraft.util.Identifier
import org.apache.commons.lang3.Validate
import org.apache.logging.log4j.{LogManager, Logger}

@SuppressWarnings(Array("unchecked")) object Registry {
	private val DEFAULT_ENTRIES = Maps.newLinkedHashMap
	val REGISTRIES: MutableRegistry[MutableRegistry[AnyRef]] = new Registry[MutableRegistry[AnyRef]]
	protected val LOGGER: Logger = LogManager.getLogger
}

@SuppressWarnings(Array("unchecked")) class Registry[T] extends MutableRegistry[T] with Codec[T] {
	final protected val indexedEntries = new Int2ObjectArrayMap[T](256)
	final protected val entries: BiMap[Identifier, T] = HashBiMap.create
	protected var randomEntries: Array[AnyRef] = _
	protected var lifecycle: Lifecycle = Lifecycle.stable
	private var nextId = 0

	def containsId(id: Identifier): Boolean = this.entries.containsKey(id)

	override def set[V <: T](rawId: Int, id: Identifier, entry: V): V = {
		this.indexedEntries.put(rawId, entry)
		Validate.notNull(id)
		Validate.notNull(entry)
		this.randomEntries = null
		this.entries.put(id, entry)
		if (this.nextId <= rawId) this.nextId = rawId + 1
		entry
	}

	override def add[V <: T](id: Identifier, entry: V): V = this.set(this.nextId, id, entry)

	override def isEmpty: Boolean = this.entries.isEmpty

	def setLifecycle(lifecycle: Lifecycle): Unit = {
		this.lifecycle = lifecycle
	}

	def getLifecycle: Lifecycle = this.lifecycle

	override def getRandom(random: Random): Optional[T] = {
		if (this.randomEntries == null) {
			val collection = this.entries.values
			if (collection.isEmpty) return Optional.empty()
			this.randomEntries = collection.toArray(new Array[AnyRef](0))
		}
		Optional.ofNullable(this.randomEntries(random.nextInt(this.randomEntries.length)).asInstanceOf[T])
	}

	override def getIds: ImmutableSet[Identifier] = ImmutableSet.copyOf(this.entries.keySet)

	@Nullable override def getId(entry: T): Identifier = this.entries.inverse.get(entry)

	override def getRawId(@Nullable entry: T): Int = this.indexedEntries.getId(entry)

	override def get(key: Identifier): T = this.entries.get(key)

	def get(index: Int): T = this.indexedEntries.get(index)

	override def put(key: Identifier, value: T): Unit = {
		this.add(key, value)
	}

	@Nonnull override def iterator: util.Iterator[T] = this.indexedEntries.iterator

	override def decode[T1](ops: DynamicOps[T1], input: T1): DataResult[Pair[T, T1]] = {
		if (ops.compressMaps) return ops.getNumberValue(input).flatMap((number: Number) => {
			def foo(number: Number): DataResult[Pair[T, T1]] = {
				val `object` = this.get(number.intValue)
				if (`object` == null) DataResult.error("Unknown registry id: " + number)
				else DataResult.success(`object`, Lifecycle.stable)
			}

			foo(number)
		}).map((`object`: T) => Pair.of(`object`, ops.empty))
		StringMappedCodecs.IDENTIFIER.decode(ops, input).flatMap((pair: Pair[Identifier, T1]) => {
			def foo(pair: Pair[Identifier, T1]): DataResult[Pair[T, T1]] = {
				val `object` = this.get(pair.getFirst)
				if (`object` == null) DataResult.error("Unknown registry key: " + pair.getFirst)
				else DataResult.success(Pair.of(`object`, pair.getSecond), Lifecycle.stable)
			}

			foo(pair)
		})
	}

	override def encode[T1](input: T, ops: DynamicOps[T1], prefix: T1): DataResult[T1] = {
		val identifier = this.getId(input)
		if (identifier == null) return DataResult.error("Unknown registry element " + input)
		if (ops.compressMaps) return ops.mergeToPrimitive(prefix, ops.createInt(this.getRawId(input))).setLifecycle(this.lifecycle)
		ops.mergeToPrimitive(prefix, ops.createString(identifier.toString)).setLifecycle(this.lifecycle)
	}
}
