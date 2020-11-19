package io.github.legacy_fabric_community.shadowedconfig.configmanagers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import com.mojang.serialization.Codec;
import io.github.legacy_fabric_community.shadowedconfig.jankson.JanksonOps;

public class JanksonConfigManager<T> extends ConfigManager<T> {
	private static final Jankson JANKSON = Jankson.builder().build();
	private T defaultValue = null;

	private JanksonConfigManager(Path configPath, Codec<T> codec) {
		super(configPath, codec);
	}

	protected JanksonConfigManager(Path configPath, Codec<T> codec, T defaultValue) {
		this(configPath, codec);
		this.defaultValue = Objects.requireNonNull(defaultValue);
	}

	protected JanksonConfigManager(Path configPath, Codec<T> codec, JsonObject defaultValue) {
		this(configPath, codec);
		this.defaultValue = codec.decode(JanksonOps.INSTANCE, Objects.requireNonNull(defaultValue)).getOrThrow(false, PRINT_TO_STDERR).getFirst();
	}

	@Override
	public void serialize() throws IOException {
		Files.write(
				this.configPath,
				this.codec.encodeStart(
						JanksonOps.INSTANCE,
						this.config
				)
						.getOrThrow(
								false,
								PRINT_TO_STDERR
						)
						.toJson(
								true,
								true
						)
						.getBytes(StandardCharsets.UTF_8)
		);
	}

	@Override
	public void deserialize() throws IOException {
		try {
			this.config = this.codec.decode(JanksonOps.INSTANCE, JANKSON.load(Files.newInputStream(this.configPath))).getOrThrow(false, PRINT_TO_STDERR).getFirst();
		} catch (SyntaxError syntaxError) {
			throw new IOException(syntaxError);
		}
	}

	@Override
	protected void writeDefaultData() throws IOException {
		byte[] bytes = "{}".getBytes(StandardCharsets.UTF_8);
		if (this.defaultValue != null) {
			bytes = this.codec.encodeStart(JanksonOps.INSTANCE, this.defaultValue).getOrThrow(false, PRINT_TO_STDERR).toJson(true, true).getBytes(StandardCharsets.UTF_8);
		}
		Files.write(this.configPath, bytes);
	}

	public void serialize(T config) throws IOException {
		Files.write(this.configPath, this.getCodec().<JsonElement>encodeStart(JanksonOps.INSTANCE, config).getOrThrow(false, PRINT_TO_STDERR).toJson().getBytes(StandardCharsets.UTF_8));
	}
}
