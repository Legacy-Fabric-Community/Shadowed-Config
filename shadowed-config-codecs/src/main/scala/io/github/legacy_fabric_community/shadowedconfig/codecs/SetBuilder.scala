package io.github.legacy_fabric_community.shadowedconfig.codecs

import java.util.function.UnaryOperator
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder

trait SetBuilder[T] {
	def ops: DynamicOps[T]

	def build(prefix: T): DataResult[T]

	def add(value: T): SetBuilder[T]

	def add(value: DataResult[T]): SetBuilder[T]

	def withErrorsFrom(result: DataResult[_]): SetBuilder[T]

	def mapError(onError: UnaryOperator[String]): SetBuilder[T]

	def build(prefix: DataResult[T]): DataResult[T] = prefix.flatMap((prefix: T) => {
		this.build(prefix)
	})

	def add[E](value: E, encoder: Encoder[E]): SetBuilder[T] = this.add(encoder.encodeStart(this.ops, value))

	def addAll[E](values: java.lang.Iterable[E], encoder: Encoder[E]): SetBuilder[T] = {
		values.forEach((v: E) => encoder.encode(v, this.ops, this.ops.empty))
		this
	}
}
