package io.github.legacy_fabric_community.shadowedconfig.nbt

import java.math.{BigDecimal, BigInteger}
import java.nio.ByteBuffer
import java.util
import java.util.Objects
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import java.util.function.{BiConsumer, Consumer}
import java.util.stream.{IntStream, Stream}

import com.google.common.annotations.Beta
import com.google.common.collect.{Iterators, Lists, Maps}
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.{DataResult, DynamicOps, MapLike, RecordBuilder}
import io.github.legacy_fabric_community.shadowedconfig.codecs.ops.ExtendedOps
import io.github.legacy_fabric_community.shadowedconfig.nbt.mixin.{CompoundTagAccessor, EndTagAccessor, ListTagAccessor, TagAccessor}
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.nbt._

//noinspection DuplicatedCode
@Beta
object NbtOps extends ExtendedOps[Tag] {
	// FOR JAVA USE ONLY
	val instance: NbtOps.type = this

	private[nbt] val THE_END_TAG = EndTagAccessor.create

	private def createListTag(b: Byte, c: Byte): Tag = {
		if (matches(b, c, 1.toByte)) return new ByteArrayTag(new Array[Byte](0))
		if (matches(b, c, 3.toByte)) new IntArrayTag(new Array[Int](0))
		else new ListTag
	}

	private def matches(b: Byte, c: Byte, d: Byte) = b == d && (c == d || c == 0)

	private def fill(listTag: ListTag, tag: Tag, tag2: Tag): Unit = {
		tag match {
			case abstractListTag2: ListTag =>
				abstractListTag2.asInstanceOf[ListTagAccessor].getValue.forEach((tag4: Tag) => {
					listTag.add(tag4)
				})
			case _ =>
		}
		listTag.add(tag2)
	}

	private def fillAll(listTag: ListTag, tag: Tag, list: util.List[Tag]): Unit = {
		tag match {
			case tempList: ListTag =>
				tempList.asInstanceOf[ListTagAccessor].getValue.forEach((tag4: Tag) => {
					listTag.add(tag4)
				})
			case _ =>
		}
		list.forEach((tag4: Tag) => {
			listTag.add(tag4)
		})
	}

	override def empty: Tag = NbtOps.THE_END_TAG

	override def convertTo[U](dynamicOps: DynamicOps[U], tag: Tag): U = tag.getType match {
		case NbtType.END =>
			dynamicOps.empty
		case NbtType.BYTE =>
			dynamicOps.createByte(tag.asInstanceOf[Tag.NumberTag].getByte)
		case NbtType.SHORT =>
			dynamicOps.createShort(tag.asInstanceOf[Tag.NumberTag].getShort)
		case NbtType.INT =>
			dynamicOps.createInt(tag.asInstanceOf[Tag.NumberTag].getInt)
		case NbtType.LONG =>
			dynamicOps.createLong(tag.asInstanceOf[Tag.NumberTag].getLong)
		case NbtType.FLOAT =>
			dynamicOps.createFloat(tag.asInstanceOf[Tag.NumberTag].getFloat)
		case NbtType.DOUBLE =>
			dynamicOps.createDouble(tag.asInstanceOf[Tag.NumberTag].getDouble)
		case NbtType.BYTE_ARRAY =>
			dynamicOps.createByteList(ByteBuffer.wrap(tag.asInstanceOf[ByteArrayTag].getArray))
		case NbtType.STRING =>
			dynamicOps.createString(tag.asInstanceOf[TagAccessor].invokeAsString)
		case NbtType.LIST =>
			this.convertList(dynamicOps, tag)
		case NbtType.COMPOUND =>
			this.convertMap(dynamicOps, tag)
		case NbtType.INT_ARRAY =>
			dynamicOps.createIntList(util.Arrays.stream(tag.asInstanceOf[IntArrayTag].getIntArray))
		case _ =>
			throw new IllegalStateException("Unknown tag type: " + tag)
	}

	override def getNumberValue(tag: Tag): DataResult[Number] = if (tag.isInstanceOf[Tag.NumberTag]) DataResult.success(tag.asInstanceOf[NumericTag].getNumber)
	else DataResult.error("Not a number")

	override def createNumeric(number: Number): Tag = if (number.isInstanceOf[Integer] || number.isInstanceOf[BigInteger] || number.isInstanceOf[AtomicInteger]) this.createInt(number.intValue)
	else if (number.isInstanceOf[Float]) this.createFloat(number.floatValue)
	else if (number.isInstanceOf[Byte]) this.createByte(number.byteValue)
	else if (number.isInstanceOf[Long] || number.isInstanceOf[AtomicLong]) this.createLong(number.longValue)
	else if (number.isInstanceOf[Short]) this.createShort(number.shortValue)
	else if (number.isInstanceOf[Double] || number.isInstanceOf[BigDecimal]) this.createDouble(number.doubleValue)
	else throw new UnsupportedOperationException("Only Primitive Numbers are supported!")

	override def createByte(b: Byte) = new ByteTag(b)

	override def createShort(s: Short) = new ShortTag(s)

	override def createInt(i: Int) = new IntTag(i)

	override def createLong(l: Long) = new LongTag(l)

	override def createFloat(f: Float) = new FloatTag(f)

	override def createDouble(d: Double) = new DoubleTag(d)

	override def createBoolean(bl: Boolean) = new ByteTag((if (bl) 1
	else 0).toByte)

	override def getStringValue(tag: Tag): DataResult[String] = if (tag.isInstanceOf[StringTag]) DataResult.success(tag.asInstanceOf[TagAccessor].invokeAsString)
	else DataResult.error("Not a string")

	override def createString(string: String) = new StringTag(string)

	override def mergeToList(tag: Tag, tag2: Tag): DataResult[Tag] = if (!tag.isInstanceOf[ListTag] && !tag.isInstanceOf[EndTag]) DataResult.error("mergeToList called with not a list: " + tag, tag)
	else {
		val listTag = NbtOps.createListTag((tag match {
			case tag1: ListTag => tag1.getElementType
			case _ => 0
		}).toByte, tag2.getType)
		listTag match {
			case tag1: ListTag => NbtOps.fill(tag1, tag, tag2)
			case _ =>
		}
		DataResult.success(listTag)
	}

	override def mergeToList(tag: Tag, list: util.List[Tag]): DataResult[Tag] = if (!tag.isInstanceOf[ListTag] && !tag.isInstanceOf[EndTag]) DataResult.error("mergeToList called with not a list: " + tag, tag)
	else {
		val abstractListTag = NbtOps.createListTag(tag match {
			case tag1: ListTag => tag1.getElementType.toByte
			case _ => 0.toByte
		}, list.stream.findFirst.map((tag: Tag) => {
			tag.getType
		}).orElse(0.toByte))
		abstractListTag match {
			case tag1: ListTag => NbtOps.fillAll(tag1, tag, list)
			case _ =>
		}
		DataResult.success(abstractListTag)
	}

	override def mergeToMap(mapTag: Tag, keyTag: Tag, tag: Tag): DataResult[Tag] = if (!mapTag.isInstanceOf[CompoundTag] && !mapTag.isInstanceOf[EndTag]) DataResult.error("mergeToMap called with not a map: " + mapTag, mapTag)
	else if (!keyTag.isInstanceOf[StringTag]) DataResult.error("key is not a string: " + keyTag, mapTag)
	else {
		val compoundTag = new CompoundTag
		mapTag match {
			case compoundTag2: CompoundTag =>
				compoundTag2.getKeys.forEach((string: String) => compoundTag.put(string, compoundTag2.get(string)))
			case _ =>
		}
		compoundTag.put(keyTag.asInstanceOf[TagAccessor].invokeAsString, tag)
		DataResult.success(compoundTag)
	}

	override def mergeToMap(tag: Tag, mapLike: MapLike[Tag]): DataResult[Tag] = if (!tag.isInstanceOf[CompoundTag] && !tag.isInstanceOf[EndTag]) DataResult.error("mergeToMap called with not a map: " + tag, tag)
	else {
		val compoundTag = new CompoundTag
		tag match {
			case compoundTag2: CompoundTag =>
				compoundTag2.getKeys.forEach((string: String) => compoundTag.put(string, compoundTag2.get(string)))
			case _ =>
		}
		val list : util.ArrayList[Tag] = Lists.newArrayList
		mapLike.entries.forEach((pair: Pair[Tag, Tag]) => {
			def foo(pair: Pair[Tag, Tag]) = {
				val tag2 = pair.getFirst
				if (!tag.isInstanceOf[StringTag]) list.add(tag)
				else compoundTag.put(tag2.asInstanceOf[TagAccessor].invokeAsString, pair.getSecond)
			}

			foo(pair)
		})
		if (list.isEmpty) DataResult.error("some keys are not strings: " + list, compoundTag)
		else DataResult.success(compoundTag)
	}

	override def getMapValues(tag: Tag): DataResult[Stream[Pair[Tag, Tag]]] = if (!tag.isInstanceOf[CompoundTag]) DataResult.error("Not a map: " + tag)
	else {
		val compoundTag = tag.asInstanceOf[CompoundTag]
		DataResult.success(compoundTag.getKeys.stream.map((string: String) => Pair.of(this.createString(string), compoundTag.get(string))))
	}

	override def getMapEntries(tag: Tag): DataResult[Consumer[BiConsumer[Tag, Tag]]] = if (!tag.isInstanceOf[CompoundTag]) DataResult.error("Not a map: " + tag)
	else {
		val compoundTag = tag.asInstanceOf[CompoundTag]
		DataResult.success((consumer: BiConsumer[Tag, Tag]) => compoundTag.getKeys.forEach((string: String) => consumer.accept(this.createString(string), compoundTag.get(string))))
	}

	override def getMap(tag: Tag): DataResult[MapLike[Tag]] = if (!tag.isInstanceOf[CompoundTag]) DataResult.error("Not a map: " + tag)
	else {
		val compoundTag = tag.asInstanceOf[CompoundTag]
		DataResult.success(new MapLike[Tag]() {
			override def get(tag: Tag): Tag = compoundTag.get(tag.asInstanceOf[TagAccessor].invokeAsString)

			override

			def get(string: String): Tag = compoundTag.get(string)

			override

			def entries: Stream[Pair[Tag, Tag]] = compoundTag.getKeys.stream.map((string: String) => Pair.of(NbtOps.this.createString(string), compoundTag.get(string)))

			override

			def toString: String = "MapLike[" + compoundTag + "]"
		})
	}

	override def createMap(stream: Stream[Pair[Tag, Tag]]): Tag = {
		val compoundTag = new CompoundTag
		stream.forEach((pair: Pair[Tag, Tag]) => compoundTag.put(pair.getFirst.asInstanceOf[TagAccessor].invokeAsString, pair.getSecond))
		compoundTag
	}

	override def getStream(tag: Tag): DataResult[Stream[Tag]] = if (tag.isInstanceOf[ListTag]) DataResult.success(tag.asInstanceOf[ListTagAccessor].getValue.stream)
	else DataResult.error("Not a list")

	override def getList(tag: Tag): DataResult[Consumer[Consumer[Tag]]] = tag match {
		case listTag: ListTag =>
			DataResult.success((consumer: Consumer[Tag]) => {
				listTag.asInstanceOf[ListTagAccessor].getValue.forEach(consumer)
			})
		case _ => DataResult.error("Not a list: " + tag)
	}

	override def getByteBuffer(tag: Tag): DataResult[ByteBuffer] = tag match {
		case tag1: ByteArrayTag => DataResult.success(ByteBuffer.wrap(tag1.getArray))
		case _ => super.getByteBuffer(tag)
	}

	override def createByteList(byteBuffer: ByteBuffer) = new ByteArrayTag(DataFixUtils.toArray(byteBuffer))

	override def getIntStream(tag: Tag): DataResult[IntStream] = tag match {
		case tag1: IntArrayTag => DataResult.success(util.Arrays.stream(tag1.getIntArray))
		case _ => super.getIntStream(tag)
	}

	override def createIntList(intStream: IntStream) = new IntArrayTag(intStream.toArray)

	override def createList(stream: Stream[Tag]): Tag = {
		val peekingIterator = Iterators.peekingIterator(stream.iterator)
		if (!peekingIterator.hasNext) new ListTag
		else {
			val tag = peekingIterator.peek
			var list3: util.ArrayList[Tag] = null
			tag match {
				case _: ByteTag =>
					list3 = Lists.newArrayList(Iterators.transform(peekingIterator, (tagx: Tag) => if (tagx != null) tagx.asInstanceOf[ByteTag].getByte
					else 0))
					val arr = new Array[Byte](list3.size)
					for (a <- arr.indices) {
						arr(a) = list3.get(a).asInstanceOf[Byte]
					}
					new ByteArrayTag(arr)
				case _: IntTag =>
					list3 = Lists.newArrayList(Iterators.transform(peekingIterator, (tagx: Tag) => if (tagx != null) tagx.asInstanceOf[IntTag].getInt
					else 0))
					val arr = new Array[Int](list3.size)
					for (a <- arr.indices) {
						arr(a) = list3.get(a).asInstanceOf[Int]
					}
					new IntArrayTag(arr)
				case _ =>
					val listTag = new ListTag
					while ( {
						peekingIterator.hasNext
					}) {
						val tag2 = peekingIterator.next
						if (!tag2.isInstanceOf[EndTag]) listTag.add(tag2)
					}
					listTag
			}
		}
	}

	override def remove(tag: Tag, string: String): Tag = tag match {
		case compoundTag: CompoundTag =>
			val compoundTag2 = new CompoundTag
			compoundTag.getKeys.stream.filter((string2: String) => !Objects.equals(string2, string)).forEach((stringx: String) => compoundTag2.put(stringx, compoundTag.get(stringx)))
			compoundTag2
		case _ => tag
	}

	override def toString = "Nbt"

	override def mapBuilder = new MapBuilder

	private[nbt] class MapBuilder extends RecordBuilder.AbstractStringBuilder[Tag, CompoundTag](NbtOps.this) {
		override protected def initBuilder = new CompoundTag

		override protected def append(string: String, tag: Tag, compoundTag: CompoundTag): CompoundTag = {
			compoundTag.put(string, tag)
			compoundTag
		}

		override protected def build(compoundTag: CompoundTag, tag: Tag): DataResult[Tag] = if (tag != null && (tag ne NbtOps.THE_END_TAG)) if (!tag.isInstanceOf[CompoundTag]) DataResult.error("mergeToMap called with not a map: " + tag, tag)
		else {
			val compoundTag2 = new CompoundTag
			compoundTag.asInstanceOf[CompoundTagAccessor].setData(Maps.newHashMap(tag.asInstanceOf[CompoundTagAccessor].getData))
			compoundTag.asInstanceOf[CompoundTagAccessor].getData.entrySet.forEach((stringTagEntry: util.Map.Entry[String, Tag]) => {
				compoundTag2.put(stringTagEntry.getKey, stringTagEntry.getValue)
			})

			DataResult.success(compoundTag2)
		}
		else DataResult.success(compoundTag)
	}
}
