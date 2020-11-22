package io.github.legacy_fabric_community.shadowedconfig.configmanagers

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.function.Consumer
import java.util.{Objects, Optional}

import blue.endless.jankson.{Jankson, JsonElement, JsonObject}
import blue.endless.jankson.impl.SyntaxError
import com.mojang.serialization.Codec
import io.github.legacy_fabric_community.shadowedconfig.configmanagers.JanksonConfigManager.PRINT_TO_STDERR
import io.github.legacy_fabric_community.shadowedconfig.jankson.JanksonOps


object JanksonConfigManager {
	private val JANKSON = Jankson.builder.build

	private val PRINT_TO_STDERR: Consumer[String] = (str: String) => {
		println(str)
	}
}

class JanksonConfigManager[T] (override val configPath: Path, override val codec: Codec[T]) extends ConfigManager[T](configPath, codec) {
	private var defaultValue: T = _

	def this(configPath: Path, codec: Codec[T], defaultValue: T) {
		this(configPath, codec)
		this.defaultValue = Objects.requireNonNull(defaultValue)
	}

	def this(configPath: Path, codec: Codec[T], defaultValue: JsonObject) {
		this(configPath, codec)
		this.defaultValue = codec.decode(JanksonOps.instance, Objects.requireNonNull(defaultValue)).getOrThrow(false, PRINT_TO_STDERR).getFirst
	}

	@throws[IOException]
	override def serialize(): Unit = {
		Files.write(this.configPath, this.codec.encodeStart(JanksonOps.instance, this.config.get()).getOrThrow(false, PRINT_TO_STDERR).toJson(true, true).getBytes(StandardCharsets.UTF_8))
	}

	@throws[IOException]
	override def deserialize(): Unit = {
		try this.config = Optional.of(this.codec.decode(JanksonOps, JanksonConfigManager.JANKSON.load(Files.newInputStream(this.configPath))).getOrThrow(false, PRINT_TO_STDERR).getFirst)
		catch {
			case syntaxError: SyntaxError =>
				throw new IOException(syntaxError)
		}
	}

	@throws[IOException]
	override def writeDefaultData(): Unit = {
		var bytes = "{}".getBytes(StandardCharsets.UTF_8)
		if (this.defaultValue != null) bytes = this.codec.encodeStart(JanksonOps, this.defaultValue).getOrThrow(false, PRINT_TO_STDERR).toJson(true, true).getBytes(StandardCharsets.UTF_8)
		Files.write(this.configPath, bytes)
	}

	@throws[IOException]
	def serialize(config: T): Unit = {
		Files.write(this.configPath, this.codec.encodeStart[JsonElement](JanksonOps, config).getOrThrow(false, PRINT_TO_STDERR).toJson.getBytes(StandardCharsets.UTF_8))
	}
}
