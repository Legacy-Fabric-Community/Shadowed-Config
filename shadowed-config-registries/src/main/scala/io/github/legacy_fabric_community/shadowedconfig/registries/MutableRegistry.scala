package io.github.legacy_fabric_community.shadowedconfig.registries

import net.minecraft.util.Identifier

trait MutableRegistry[T] extends IdentifiableRegistry[T] {
	def set[V <: T](rawId: Int, id: Identifier, entry: V): V

	def add[V <: T](id: Identifier, entry: V): V

	def isEmpty: Boolean
}
