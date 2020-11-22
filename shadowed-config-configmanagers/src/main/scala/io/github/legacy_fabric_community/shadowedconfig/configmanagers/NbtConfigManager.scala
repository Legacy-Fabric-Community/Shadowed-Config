package io.github.legacy_fabric_community.shadowedconfig.configmanagers

import java.io.IOException
import java.nio.file.{Files, Path}
import java.util.Optional
import java.util.function.Consumer

import com.mojang.serialization.Codec
import io.github.legacy_fabric_community.shadowedconfig.nbt.NbtOps
import javax.annotation.Nonnull
import net.minecraft.nbt.{CompoundTag, NbtIo}

object NbtConfigManager {
	protected val PRINT_TO_STDERR: Consumer[String] = (str: String) => {
		println(str)
	}
}

class NbtConfigManager[T] (override val configPath: Path, override val codec: Codec[T], @Nonnull val defaultValue: T) extends ConfigManager[T](configPath, codec) {
	@throws[IOException]
	override def serialize(): Unit = {
		NbtIo.writeCompressed(this.codec.encodeStart(NbtOps.INSTANCE, this.getConfig).getOrThrow(false, NbtConfigManager.PRINT_TO_STDERR).asInstanceOf[CompoundTag], Files.newOutputStream(this.configPath))
	}

	@throws[IOException]
	override def deserialize(): Unit = {
		this.config = (Optional.of(this.codec.parse(NbtOps.INSTANCE, NbtIo.readCompressed(Files.newInputStream(this.configPath))).getOrThrow(false, NbtConfigManager.PRINT_TO_STDERR)))
	}

	@throws[IOException]
	override def writeDefaultData(): Unit = {
		NbtIo.writeCompressed(this.codec.encodeStart(NbtOps.INSTANCE, this.defaultValue).getOrThrow(false, NbtConfigManager.PRINT_TO_STDERR).asInstanceOf[CompoundTag], Files.newOutputStream(this.configPath))
	}

	@Nonnull def getDefaultValue: T = this.defaultValue
}
