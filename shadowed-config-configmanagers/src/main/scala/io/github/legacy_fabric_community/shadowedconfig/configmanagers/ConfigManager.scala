package io.github.legacy_fabric_community.shadowedconfig.configmanagers

import java.io.IOException
import java.nio.file.{Files, Path}
import java.util.Optional
import java.util.function.Consumer

import blue.endless.jankson.JsonObject
import com.mojang.serialization.Codec

object ConfigManager {
	protected val PRINT_TO_STDERR: Consumer[String] = (str: String) => {
		println(str)
	}

	def jankson[T](configPath: Path, codec: Codec[T], defaultValue: T) = new JanksonConfigManager[T](configPath, codec, defaultValue)

	def jankson[T](configPath: Path, codec: Codec[T], defaultValue: JsonObject) = new JanksonConfigManager[T](configPath, codec, defaultValue)

	def nbt[T](configPath: Path, codec: Codec[T], defaultValue: T) = new NbtConfigManager[T](configPath, codec, defaultValue)
}

abstract class ConfigManager[T] protected(val configPath: Path, val codec: Codec[T]) {
	this.check()
	this.deserializeQuietly()
	protected var config: Optional[T] = _

	final def getConfigPath: Path = this.configPath

	final def getCodec: Codec[T] = this.codec

	final def getConfig: T = this.config.get()

	@throws[IOException]
	def serialize(): Unit

	@throws[IOException]
	def deserialize(): Unit

	@throws[IOException]
	def writeDefaultData(): Unit

	def serializeQuietly(): Unit = {
		try this.serialize()
		catch {
			case e: IOException =>
				e.printStackTrace()
		}
	}

	def deserializeQuietly(): Unit = {
		try this.deserialize()
		catch {
			case e: IOException =>
				e.printStackTrace()
		}
	}

	protected def check(): Unit = {
		try {
			if (Files.isDirectory(this.configPath)) Files.delete(this.configPath)
			if (!Files.exists(this.configPath)) {
				Files.createFile(this.configPath)
				this.writeDefaultData()
			}
		} catch {
			case e: IOException =>
				e.printStackTrace()
		}
	}
}
