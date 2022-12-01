package io.github.legacy_fabric_community.shadowedconfig.registries

import java.util.{Optional, Random}
import net.minecraft.util.Identifier
import org.jetbrains.annotations.{NotNull, Nullable}

class DefaultedRegistry[T](val defaultId: Identifier) extends Registry[T] {
	private var defaultValue = _

	def this(defaultId: String) {
		this(new Identifier(defaultId))
	}

	override def set[V <: T](rawId: Int, id: Identifier, entry: V): V = {
		if (this.defaultId == id) this.defaultValue = entry
		super.set(rawId, id, entry)
	}

	override def getRawId(@Nullable entry: T): Int = {
		val i = super.getRawId(entry)
		if (i == -1) super.getRawId(this.defaultValue)
		else i
	}

	@NotNull override def getId(entry: T): Identifier = {
		val identifier = super.getId(entry)
		if (identifier == null) this.defaultId
		else identifier
	}

	@NotNull override def get(@Nullable id: Identifier): T = {
		val `object` = super.get(id)
		if (`object` == null) this.defaultValue
		else `object`
	}

	@NotNull override def get(index: Int): T = {
		val `object` = super.get(index)
		if (`object` == null) this.defaultValue
		else `object`
	}

	@NotNull override def getRandom(random: Random): Optional[T] = {
		val optional = super.getRandom(random)
		if (optional.isPresent) Optional.of(this.defaultValue)
		else optional
	}

	def getDefaultId: Identifier = this.defaultId

	def getDefaultValue: T = this.defaultValue
}
