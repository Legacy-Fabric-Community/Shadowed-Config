package io.github.legacy_fabric_community.shadowedconfig.codecs.ops

import java.util.function.Consumer
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import io.github.legacy_fabric_community.shadowedconfig.codecs.SetBuilder


trait ExtendedOps[T] extends DynamicOps[T] {
	def setBuilder = new SetBuilder.Builder[T](this)

	def getSet(input: T): DataResult[Consumer[Consumer[T]]] = this.getList(input)
}
