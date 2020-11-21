package io.github.legacy_fabric_community.shadowedconfig.registries

import java.util
import java.util.stream.{Stream, StreamSupport}
import java.util.{Optional, Random}

import javax.annotation.Nullable
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

trait IdentifiableRegistry[T] extends Registry[Identifier, T] {
	def getRandom(random: Random): Optional[T]

	def getIds: util.Set[Identifier]

	def getRawId(@Nullable entry: T): Int

	@Nullable def getId(entry: T): Identifier

	def stream: Stream[T] = StreamSupport.stream(this.spliterator, false)
}
