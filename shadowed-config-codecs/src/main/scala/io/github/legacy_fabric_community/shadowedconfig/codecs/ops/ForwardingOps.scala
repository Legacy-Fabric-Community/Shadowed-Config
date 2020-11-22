package io.github.legacy_fabric_community.shadowedconfig.codecs.ops

import java.nio.ByteBuffer
import java.util
import java.util.function.{BiConsumer, Consumer}
import java.util.stream.{IntStream, LongStream, Stream}

import com.mojang.datafixers.util.Pair
import com.mojang.serialization._
import io.github.legacy_fabric_community.shadowedconfig.codecs.SetBuilder

abstract class ForwardingOps[T] protected(val delegate: ExtendedOps[T]) extends ExtendedOps[T] {
	override def empty: T = this.delegate.empty

	override def convertTo[U](dynamicOps: DynamicOps[U], `object`: T): U = this.delegate.convertTo(dynamicOps, `object`)

	override def getNumberValue(`object`: T): DataResult[Number] = this.delegate.getNumberValue(`object`)

	override def createNumeric(number: Number): T = this.delegate.createNumeric(number)

	override def createByte(b: Byte): T = this.delegate.createByte(b)

	override def createShort(s: Short): T = this.delegate.createShort(s)

	override def createInt(i: Int): T = this.delegate.createInt(i)

	override def createLong(l: Long): T = this.delegate.createLong(l)

	override def createFloat(f: Float): T = this.delegate.createFloat(f)

	override def createDouble(d: Double): T = this.delegate.createDouble(d)

	override def getBooleanValue(`object`: T): DataResult[java.lang.Boolean] = this.delegate.getBooleanValue(`object`)

	override def createBoolean(bl: Boolean): T = this.delegate.createBoolean(bl)

	override def getStringValue(`object`: T): DataResult[String] = this.delegate.getStringValue(`object`)

	override def createString(string: String): T = this.delegate.createString(string)

	override def mergeToList(`object`: T, object2: T): DataResult[T] = this.delegate.mergeToList(`object`, object2)

	override def mergeToList(`object`: T, list: util.List[T]): DataResult[T] = this.delegate.mergeToList(`object`, list)

	override def mergeToMap(`object`: T, object2: T, object3: T): DataResult[T] = this.delegate.mergeToMap(`object`, object2, object3)

	override def mergeToMap(`object`: T, mapLike: MapLike[T]): DataResult[T] = this.delegate.mergeToMap(`object`, mapLike)

	override def getMapValues(`object`: T): DataResult[Stream[Pair[T, T]]] = this.delegate.getMapValues(`object`)

	override def getMapEntries(`object`: T): DataResult[Consumer[BiConsumer[T, T]]] = this.delegate.getMapEntries(`object`)

	override def createMap(stream: Stream[Pair[T, T]]): T = this.delegate.createMap(stream)

	override def getMap(`object`: T): DataResult[MapLike[T]] = this.delegate.getMap(`object`)

	override def getStream(`object`: T): DataResult[Stream[T]] = this.delegate.getStream(`object`)

	override def getList(`object`: T): DataResult[Consumer[Consumer[T]]] = this.delegate.getList(`object`)

	override def createList(stream: Stream[T]): T = this.delegate.createList(stream)

	override def getByteBuffer(`object`: T): DataResult[ByteBuffer] = this.delegate.getByteBuffer(`object`)

	override def createByteList(byteBuffer: ByteBuffer): T = this.delegate.createByteList(byteBuffer)

	override def getIntStream(`object`: T): DataResult[IntStream] = this.delegate.getIntStream(`object`)

	override def createIntList(intStream: IntStream): T = this.delegate.createIntList(intStream)

	override def getLongStream(`object`: T): DataResult[LongStream] = this.delegate.getLongStream(`object`)

	override def createLongList(longStream: LongStream): T = this.delegate.createLongList(longStream)

	override def remove(`object`: T, string: String): T = this.delegate.remove(`object`, string)

	override def compressMaps: Boolean = this.delegate.compressMaps

	override def listBuilder: ListBuilder[T] = this.delegate.listBuilder

	override def setBuilder(): SetBuilder[T] = this.delegate.setBuilder()

	override def getSet(input: T): DataResult[Consumer[Consumer[T]]] = this.delegate.getSet(input)

	override def mapBuilder: RecordBuilder[T] = this.delegate.mapBuilder
}
