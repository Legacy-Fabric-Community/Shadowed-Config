package io.github.legacy_fabric_community.shadowedconfig.jankson

import java.math.BigDecimal
import java.util.stream.Stream
import java.util.{Map, Objects}
import java.{lang, util}

import blue.endless.jankson._
import com.google.common.collect.Lists
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.{DataResult, DynamicOps, MapLike}
import io.github.legacy_fabric_community.shadowedconfig.codecs.ops.ExtendedOps

//noinspection DuplicatedCode
object JanksonOps extends ExtendedOps[JsonElement] {
	// FOR JAVA USE ONLY
	val instance: JanksonOps.type = this

	override def empty: JsonElement = JsonNull.INSTANCE

	override def createString(value: String) = new JsonPrimitive(value)

	override def createNumeric(i: Number) = new JsonPrimitive(i)

	override def createBoolean(value: Boolean) = new JsonPrimitive(value)

	override def convertTo[U](outOps: DynamicOps[U], input: JsonElement): U = {
		input match {
			case _: JsonObject => return this.convertMap(outOps, input)
			case _: JsonArray => return this.convertList(outOps, input)
			case _: JsonNull => return outOps.empty
			case _ =>
		}
		val inputPrimitive = input.asInstanceOf[JsonPrimitive]
		inputPrimitive.getValue match {
			case _: String => return outOps.createString(String.valueOf(inputPrimitive.getValue))
			case bool: java.lang.Boolean => return outOps.createBoolean(bool)
			case _ =>
		}
		val numberExact = inputPrimitive.getValue match {
			case decimal: BigDecimal => decimal
			case _ => new BigDecimal(inputPrimitive.getValue.toString)
		}
		try {
			val l = numberExact.longValueExact
			if (l.toByte == l) return outOps.createByte(l.toByte)
			if (l.toShort == l) return outOps.createShort(l.toShort)
			if (l.toInt == l) return outOps.createInt(l.toInt)
			outOps.createLong(l)
		} catch {
			case e: ArithmeticException =>
				val d = numberExact.doubleValue
				if (d.toFloat == d) return outOps.createFloat(d.toFloat)
				outOps.createDouble(d)
		}
	}

	override def getNumberValue(input: JsonElement): DataResult[Number] = {
		input match {
			case primitive: JsonPrimitive => primitive.getValue match {
				case number: Number => return DataResult.success(number)
				case _: lang.Boolean => return DataResult.success(if (primitive.getValue.asInstanceOf[Boolean]) 1.toByte
				else 0.toByte)
				case _ =>
			}
			case _ =>
		}
		DataResult.error("Not a number: " + input)
	}

	override def getBooleanValue(input: JsonElement): DataResult[java.lang.Boolean] = {
		input match {
			case primitive: JsonPrimitive => primitive.getValue match {
				case _: lang.Boolean => return DataResult.success(primitive.getValue.asInstanceOf[Boolean])
				case _: Number => return super.getBooleanValue(input)
				case _ =>
			}
			case _ =>
		}
		DataResult.error("Not a boolean: " + input)
	}

	override def getStringValue(input: JsonElement): DataResult[String] = {
		input match {
			case primitive: JsonPrimitive if primitive.getValue.isInstanceOf[String] => return DataResult.success(String.valueOf(primitive.getValue))
			case _ =>
		}
		DataResult.error("Not a string: " + input)
	}

	override def mergeToList(list: JsonElement, value: JsonElement): DataResult[JsonElement] = {
		if (!list.isInstanceOf[JsonArray] && (list ne this.empty)) return DataResult.error("mergeToList not called with a list: " + list, list)
		val array = new JsonArray
		if (list ne this.empty) { //noinspection ConstantConditions
			array.addAll(list.asInstanceOf[JsonArray])
		}
		array.add(value)
		DataResult.success(array)
	}

	override def mergeToMap(map: JsonElement, key: JsonElement, value: JsonElement): DataResult[JsonElement] = {
		if (!map.isInstanceOf[JsonObject] && (map ne this.empty)) return DataResult.error("mergeToMap not called with a map: " + map, map)
		if (!key.isInstanceOf[JsonPrimitive] || !(key.asInstanceOf[JsonPrimitive]).getValue.isInstanceOf[String]) return DataResult.error("Key is not a string: " + key, map)
		val output = new JsonObject
		if (map ne this.empty) map.asInstanceOf[JsonObject].forEach((key: String, element: JsonElement) => {
			output.put(key, element)
		})
		output.put(String.valueOf(key.asInstanceOf[JsonPrimitive].getValue), value)
		DataResult.success(output)
	}

	override def mergeToMap(map: JsonElement, values: MapLike[JsonElement]): DataResult[JsonElement] = {
		if (!map.isInstanceOf[JsonObject] && (map ne this.empty)) return DataResult.error("mergeToMap not called with a map: " + map, map)
		val newMap = new JsonObject
		if (map ne this.empty) map.asInstanceOf[JsonObject].forEach((key: String, element: JsonElement) => {
			newMap.put(key, element)
		})
		val missed : util.List[JsonElement] = Lists.newArrayList
		values.entries.forEach((entry: Pair[JsonElement, JsonElement]) => {
			def call(entry: Pair[JsonElement, JsonElement]): Any = {
				val key = entry.getFirst
				if (!key.isInstanceOf[JsonPrimitive] || !(map.asInstanceOf[JsonPrimitive]).getValue.isInstanceOf[String]) {
					missed.add(key)
					return
				}
				newMap.put(String.valueOf(key.asInstanceOf[JsonPrimitive].getValue), entry.getSecond)
			}

			call(entry)
		})
		if (!missed.isEmpty) return DataResult.error("Keys are not strings: " + missed, newMap)
		DataResult.success(newMap)
	}

	override def getMapValues(input: JsonElement): DataResult[Stream[Pair[JsonElement, JsonElement]]] = {
		if (!input.isInstanceOf[JsonObject]) return DataResult.error("Not a json object: " + input)
		DataResult.success((input.asInstanceOf[JsonObject]).entrySet.stream.map((entry: util.Map.Entry[String, JsonElement]) => Pair.of(new JsonPrimitive(entry.getKey), if (entry.getValue.isInstanceOf[JsonNull]) null
		else entry.getValue)))
	}

	override def createMap(pairStream: Stream[Pair[JsonElement, JsonElement]]): JsonElement = {
		val jsonObject = new JsonObject
		pairStream.forEach((p: Pair[JsonElement, JsonElement]) => jsonObject.put(p.getFirst.toJson, p.getSecond))
		jsonObject
	}

	override def getStream(input: JsonElement): DataResult[Stream[JsonElement]] = {
		input match {
			case array: JsonArray => return DataResult.success(array.stream.map((e: JsonElement) => if (e.isInstanceOf[JsonNull]) null
			else e))
			case _ =>
		}
		DataResult.error("Not a json array: " + input)
	}

	override def createList(input: Stream[JsonElement]): JsonElement = {
		val array = new JsonArray
		input.forEach((i : JsonElement) => {
			array.add(i)
		})
		array
	}

	override def remove(input: JsonElement, key: String): JsonElement = {
		input match {
			case jsonObject: JsonObject =>
				val result = new JsonObject
				jsonObject.entrySet.stream.filter((entry: util.Map.Entry[String, JsonElement]) => !Objects.equals(entry.getKey, key)).forEach((entry: Map.Entry[String, JsonElement]) => result.put(entry.getKey, entry.getValue))
				return result
			case _ =>
		}
		input
	}
}
